package earth.client.Short;

/**
 * Created by Frapo on 2017/1/22.
 */

import earth.client.Util.Constant;
import earth.client.Util.Monitor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.Arrays;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static final String TAG = "earth.client.Short:";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpResponse) {
            // 在这里处理出现错误的情况（消息发送失败，网络连接失败，超时，上传终断等）
            HttpResponse response = (HttpResponse) msg;
            //log.debug("CONTENT_TYPE:" + response.headers().get(HttpHeaders.Names.CONTENT_TYPE));
            if(response.status().equals(HttpResponseStatus.BAD_GATEWAY)){
                // server is temporarily shut down
            }else if(response.status().equals(HttpResponseStatus.NOT_FOUND)){
                //404
                // Server is unavailable
                Monitor.logger(TAG,"earth.client.Client : Request 404" + Arrays.toString(((HttpResponse) msg).headers().entries().toArray()));
            }else if(response.status().equals(HttpResponseStatus.BAD_REQUEST)){
                //402
                // bad request
                Monitor.logger(TAG,"earth.client.Client : Request 400" + Arrays.toString(((HttpResponse) msg).headers().entries().toArray()));
            }else if(response.status().equals(HttpResponseStatus.FORBIDDEN)){
                //403
                // forbidden
                Monitor.logger(TAG,"earth.client.Client : Request 403" + Arrays.toString(((HttpResponse) msg).headers().entries().toArray()));
            }else if(response.status().equals(HttpResponseStatus.OK)){
                // OK
            }else if(response.status().equals(HttpResponseStatus.GATEWAY_TIMEOUT)){
                //
                Monitor.logger(TAG,"earth.client.Client : Request Timeout" + Arrays.toString(((HttpResponse) msg).headers().entries().toArray()));
            }else{
                Monitor.logger(TAG,"earth.client.Client : Request Has Failed" + Arrays.toString(((HttpResponse) msg).headers().entries().toArray()));
            }

        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            String http = buf.toString(io.netty.util.CharsetUtil.UTF_8);
            //OK
            buf.release();
            if (Constant.getHandler() != null){
                Constant.getHandler().shortReturn(http,msg);
            }
        }
    }


}
