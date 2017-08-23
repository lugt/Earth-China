package earth.client.Short;

import earth.client.Util.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.util.List;

/**
 * Created by God on 2017/2/11.
 */
public class UploadClient {
    private StringBuffer resultBuffer = new StringBuffer();
    private EventLoopGroup group = null;
    private HttpDataFactory factory = null;

    private Object waitObject = new Object();

    private ChannelFuture future = null;

    public UploadClient(String host, int port) throws Exception {

        this.group = new NioEventLoopGroup();
        this.factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

        Bootstrap b = new Bootstrap();
        b.option(ChannelOption.TCP_NODELAY, true);
        b.option(ChannelOption.SO_SNDBUF, 1048576*200);
        b.option(ChannelOption.SO_KEEPALIVE, true);

        b.group(group).channel(NioSocketChannel.class);
        b.handler(new UploadClientIntializer());

        this.future = b.connect(host, port).sync();
    }

    public int uploadFile(String uri,String path,String usign) {
        if(path == null) {
            System.out.println("上传文件的路径不能为null...");
            return -3020;
        }
        File file = new File(path);
        if (!file.canRead()) {
            System.out.println(file.getName() + "不可读...");
            return -3021;
        }
        if (file.isHidden() || !file.isFile()) {
            System.out.println(file.getName() + "不存在...");
            return -3023;
        }

        try {
            HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri);

            HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(factory, request, false);

            bodyRequestEncoder.addBodyAttribute("getform", "POST");
            bodyRequestEncoder.addBodyAttribute("usign", usign);
            bodyRequestEncoder.addBodyAttribute("random", "" + Math.random());
            bodyRequestEncoder.addBodyFileUpload("upload-file", file, "application/octet-stream", false);

            List<InterfaceHttpData> bodylist = bodyRequestEncoder.getBodyListAttributes();
            if (bodylist == null) {
                System.out.println("请求体不存在...");
                return -3010;
            }

            HttpRequest request2 = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, file.getName());
            HttpPostRequestEncoder bodyRequestEncoder2 = new HttpPostRequestEncoder(factory, request2, true);
            bodyRequestEncoder2.setBodyHttpDatas(bodylist);
            bodyRequestEncoder2.finalizeRequest();

            Channel channel = this.future.channel();
            if(channel.isActive() && channel.isWritable()) {
                channel.writeAndFlush(request2);
                if (bodyRequestEncoder2.isChunked()) {
                    channel.writeAndFlush(bodyRequestEncoder2).awaitUninterruptibly();
                }
                bodyRequestEncoder2.cleanFiles();
            }

            channel.closeFuture().sync();
            return 1000;
        } catch (Exception e) {
            e.printStackTrace();
            return -3012;
        }
    }

    public void shutdownClient() {
        // 等待数据的传输通道关闭
        group.shutdownGracefully();
        factory.cleanAllHttpData();
    }

    public boolean isCompleted() {
        while(waitObject != null) {
            //当通道处于开通和活动时，处于等待
        }
        if(resultBuffer.length() > 0) {
            if("200".equals(resultBuffer.toString())) {
                resultBuffer.setLength(0);
                return true;
            }
        }
        return false;
    }

    private class UploadClientIntializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast("decoder", new HttpResponseDecoder());
            pipeline.addLast("encoder", new HttpRequestEncoder());
            pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
            pipeline.addLast("dispatcher", new UpLoadClientHandler());
        }
    }

    private class UpLoadClientHandler extends SimpleChannelInboundHandler<HttpObject> {
        private boolean readingChunks = false;
        private int succCode = 200;

        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
                throws Exception {
            if (msg instanceof HttpResponse) {
                HttpResponse response = (HttpResponse) msg;
                succCode = response.status().code();
                if (succCode == 200 && response.headers().contains(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED, true)) {
                    readingChunks = true;
                }
            }

            if (msg instanceof HttpContent) {
                HttpContent chunk = (HttpContent) msg;
                if(chunk.content().readableBytes() > 0) {
                    System.out.println("【响应】" + succCode + ">>" + chunk.content().toString(CharsetUtil.UTF_8));
                    Constant.getHandler().shortReturn(chunk.content().toString(CharsetUtil.UTF_8), null);
                }else{
                    Constant.getHandler().shortStatus(-100041,null,msg);
                }
                if (chunk instanceof LastHttpContent) {
                    readingChunks = false;
                }
            }

            if (!readingChunks) {
                resultBuffer.append(succCode);
                ctx.channel().close();
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            waitObject = null;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            resultBuffer.setLength(0);
            resultBuffer.append(500);
            System.out.println("管道异常：" + cause.getMessage());
            Constant.getHandler().shortStatus(-10020,cause.getLocalizedMessage(),cause);
            cause.printStackTrace();
            ctx.channel().close();
        }
    }
}
