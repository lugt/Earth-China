package earth.server.peking;

/**
 * Created by Frapo on 2017/1/22.
 */

import earth.server.Constant;
import earth.server.Monitor;
import earth.server.space.facial.FacialExecutor;
import earth.server.friend.FriendExecutor;
import earth.server.user.UserInfo;
import earth.server.user.UserLogin;
import earth.server.user.UserReg;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.nio.charset.Charset;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class PekingHandler extends ChannelInboundHandlerAdapter {

    private static Log log = LogFactory.getLog(PekingHandler.class);

    private HttpRequest request;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;
            String uri = request.uri();
            Monitor.access(request.method() + " - Uri: " + uri);
        }

        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            // 在此处判断
            /**
             *  未进行任何操作
             *  @apiNote 请求错误1001
             * */
            String res = null;

            if (buf.readableBytes() >= 1000000000) {
                // Bigger Than 1MB
                res = "Exp,9001";

            } else {

                String uri = request.uri();
                int i = 0;
                if((i = uri.indexOf('?')) > -1 && uri.length() > i) {
                    String q = uri.substring(i+1);
                    buf = ByteBufAllocator.DEFAULT.ioBuffer(q.getBytes("UTF-8").length);
                    buf.writeBytes(q.getBytes("UTF-8"));
                }

                if (uri.startsWith("/sign/auto")) {
                    // 登录态恢复（本地token验证） Session 恢复
                    res = (new UserLogin()).reborn(buf);

                } else if (uri.startsWith("/sign/etid")) {
                    // 登录 通过
                    res = (new UserLogin()).etid(buf);

                } else if (uri.startsWith("/sign/phone")) {
                    // 登录 通过
                    res = (new UserLogin()).phone(buf);

                } else if (uri.startsWith("/exit")) {
                    // 登录 通过
                    res = (new UserLogin()).exit(buf);

                } else if (uri.startsWith("/basic/me")) {
                    // 用户信息
                    res = (new UserInfo()).basic(buf);

                } else if (uri.startsWith("/avatar/get")) {
                    // 指定用户头像
                    res = "Exp,-100014";

                } else if (uri.startsWith("/reg")) {
                    // 注册
                    res = (new UserReg()).create(buf);

                } else if (uri.startsWith("/id/get")) {

                    res = (new UserInfo()).getIdentity(buf);

                } else if (uri.startsWith("/id/verify")) {
                    // 身份认证

                    res = new UserInfo().verify(buf);

                } else if (uri.startsWith("/friend/list")) {
                    //获取
                    res = (new FriendExecutor()).getList(buf);

                } else if (uri.startsWith("/friend/add")) {
                    // 添加好友
                    res = (new FriendExecutor()).add(buf);

                } else if (uri.startsWith("/friend/remove")) {
                    // 添加好友
                    res = (new FriendExecutor()).remove(buf);

                }else if (uri.startsWith("/welcome")) {

                    String[] m = new String[6];
                    m[0] = "W";
                    m[1] = Constant.ServerV;
                    m[2] = Constant.ClientV;
                    m[3] = Constant.ProtoV;
                    m[4] = ((Long) (System.currentTimeMillis() / 1000)).intValue() + "";
                    m[5] = Constant.SecureEnforce;
                    res = String.join(",", m);

                } else if (uri.startsWith("/test")) {
                    // Session 持续
                    res = "T,ok,"+buf.toString(Charset.forName("UTF-8"));


                } else if (uri.startsWith("/search/cell")) {
                    // 搜索朋友
                    // 通过手机号/ETID
                    res = new UserInfo().getPublicCell(buf);

                } else if (uri.startsWith("/basic/etid")) {
                    // 通过/ETID 查看基本信息
                    res = new UserInfo().getPublicEtid(buf);

                } else if (uri.startsWith("/search/topic")) {
                    // 搜索
                    res="Top,1001";

                } else if (uri.startsWith("/face/linear")) {

                    res= (new FacialExecutor()).getLinear(buf);

                } else if (uri.startsWith("/face/upload")) {

                    res= (new FacialExecutor()).getLinear(buf);

                } else if (uri.startsWith("/update")) {

                    res= UpdateManager.getUpdate();

                } else if (uri.startsWith("/feedback")) {

                    res = UpdateManager.setLog(buf);

                } else if (uri.startsWith("/kiosk/get")) {
                    // 通过智能问答来断定真假，确保问题不重复
                    res = (new KioskExecutor()).getQ("CN");
                } else if (uri.equals("/")) {
                    res = "<h1> 欢迎 - Welcome - Bienvenue - </h1>";
                } else {
                    // cmd_taker
                    res = "Exp,1001";
                }
                buf.release();
            }

            if (res == null) {
                res = "Exp,1001";
            }

            Monitor.response(res);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                    OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");
            //response.headers().set(CONTENT_ENCODING,"");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                    response.content().readableBytes());

            //if (Values.KEEP_ALIVE.equals(request.headers().get("Connection"))) {
                //response.headers().set(CONNECTION, Values.KEEP_ALIVE);
            //}
            ctx.write(response);
            ctx.flush();
            ctx.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(!(cause.getMessage().startsWith("远程主机")) && !(cause.getMessage().startsWith("Connection"))) {
            log.error(cause.getMessage());
            cause.printStackTrace();
        }
        ctx.close();
    }

}