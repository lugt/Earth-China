package earth.server.tianjin.Server;

import earth.server.Constant;
import earth.server.Monitor;
import earth.Connection.*;
import earth.server.tianjin.Util.Ipv4Util;
import earth.server.utils.Verifier;
import io.netty.channel.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Frapo on 2017/1/22.
 * Version :20
 * Earth - Moudule ${PACKAGE_NAME}
 */
public class TianjinHandler  extends SimpleChannelInboundHandler<XLRequest>{

    private static Log log = LogFactory.getLog(Monitor.class);

    private static final int MAX_UN_REC_PING_TIMES = 10;

    private int state_unlogged = 0;


    // 失败计数器：未收到client端发送的ping请求
    private int unRecPingTimes = 0;

    // 每个chanel对应一个线程，此处用来存储对应于每个线程的一些基础数据，此处不一定要为KeepAliveMessage对象
    ThreadLocal<ClientInfo> cli = new ThreadLocal<ClientInfo>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, XLRequest msg){

        if(msg != null) {
            XLRequest req = (XLRequest)msg;
            /**if (req.getCommand() > 0x0) {
                // 失败计数器清零
                unRecPingTimes = 0;*/
            if (cli.get() == null) {
                ClientInfo ms = new ClientInfo();
                try {
                    String base = ctx.channel().remoteAddress().toString().replaceAll("/","");
                    String ips[] = base.split(":");
                    ms.ip = Ipv4Util.ipToInt(ips[0]);
                } catch (Exception e) {
                    log.error("Invalid IP : " + ctx.channel().remoteAddress().toString());
                }
                //将字节数组转换为整型
                cli.set(ms);
                // TODO 统一管理设备号码
            }

            if (MsgType.CONNCET == req.getCommand()) {
                LongServerHandler.connect(ctx,cli,req);

            } else if (cli.get().etid > Constant.MINIMAL_ETID) {
                switch (req.getCommand()) {
                    case MsgType.PING: {
                        LongServerHandler.pingAccept(ctx,cli.get(),req);
                    }
                    break;
                    case MsgType.CONNCET: {
                        LongServerHandler.ConnectAgain(ctx, cli.get(), req);
                    }
                    break;
                    case MsgType.FETCHMSG: {
                        //收到客户端的请求
                        LongServerHandler.fetchMsg(ctx,cli.get(),req);
                    }
                    break;
                    case MsgType.MSGARRIVEACK: {
                        LongServerHandler.ArriveAck(ctx, cli.get(), req);
                    }
                    break;
                    case MsgType.SEND: {
                        //收到客户端发送信息的请求
                        LongServerHandler.Send(ctx,cli.get(),req);
                    }
                    break;
                    default: {
                        LongServerHandler.error(-200010,ctx,req.getSessionid());
                    }
                }


            } else {
                // 没有CONNECT的连接，多次后直接关闭Channel
                state_unlogged++;
                if (state_unlogged >= 20) {
                    ctx.channel().close();
                    return;
                }
                //说明未登录，或者连接断了，服务器向客户端发起登录请求，让客户端重新登录
                LongServerHandler.reconnect(ctx,req);
            }
            //ReferenceCountUtil.release(rep);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(!(cause.getMessage().startsWith("远程")) && !cause.getMessage().startsWith("Connection")){
            log.error("Long - 意外:" + cause.getMessage());
            cause.printStackTrace();
        }else{
            log.error("客户端下线");
        }
        if (cli.get() != null && Verifier.isValidEtid(cli.get().etid)) {
            NettyChannelMap.remove(ctx.channel());
        }
        cli.remove();
        ctx.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //System.out.println("ShortClient active ");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 关闭，等待重连
        ctx.close();
        if (cli.get() != null && cli.get().etid > Constant.MINIMAL_ETID) {
            NettyChannelMap.remove(ctx.channel());
        }
        log.debug("===服务端===(客户端失效)");
        //channel失效，从Map中移除
        cli.remove();
        ctx.channel().close();
    }

}