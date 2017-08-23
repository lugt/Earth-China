package earth.client.Long;

import earth.Connection.XLResponse;
import earth.client.Util.Constant;
import earth.Connection.MsgType;
import earth.Connection.PingMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by Frapo on 2017/1/22.
 */
public class LongClientHandler extends ChannelInboundHandlerAdapter {

    //利用写空闲发送心跳检测消息
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE:
                    PingMsg pingMsg = new PingMsg(LongClient.sessionCount++);
                    ctx.writeAndFlush(pingMsg);
                    //Monitor.logger("=====sending ping to server----------");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable th){
        // 主要是服务器连接断开这种情况
        th.printStackTrace();
        LongClient.setLoggedin(false);
        Constant.getHandler().longStatus(-20003, 0,null);
        ctx.close();
        //LongClient.reconnect();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg){

        if(msg instanceof XLResponse) {
            //Monitor.logger("Rcvd Message on earth.client.Long");
            XLResponse rep = (XLResponse) msg;
            byte bt = rep.getCommand();
            switch (bt) {
                case MsgType.PONG: {
                    // OK
                }break;
                case MsgType.CONNSUCCESS: {
                    // OK
                    LongClient.setLoggedin(true);
                    Constant.getHandler().longStatus(-20006,rep.getResult(),rep);
                }break;
                case MsgType.RECONNCET: {
                    // OK
                    LongClient.setLoggedin(false);
                    Constant.getHandler().longStatus(-20007,rep.getResult(),rep); //rep.getValue("a")
                }break;
                default:
                    Constant.getHandler().longReturn(rep.getCommand(),rep.getResult(),rep); //rep.getValue("a")
            }
            ReferenceCountUtil.release(rep);
        }
    }
}