package earth.server.tianjin.Server;

import earth.Connection.*;
import earth.server.Constant;
import earth.server.message.MessageBean;
import earth.server.message.Messenger;
import earth.server.user.InnerLogin;
import earth.server.utils.Verifier;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by Frapo on 2017/2/5.
 * Version :22
 * Earth - Moudule earth.server.tianjin.Server
 */
public class LongServerHandler {

    static Log log = LogFactory.getLog(LongServerHandler.class);

    public static void ArriveAck(ChannelHandlerContext ctx, ClientInfo clientInfo, XLRequest req) {
        XLResponse rep = null;
        String mId = req.getValue("list");
        String[] s = mId.split(",");

        if(s.length <= 0){
            error(-100002,ctx,req.getSessionid());
        }

        try {
            long a = 0L;
            for (int i = 0; i < s.length; i++) {
                if(!Verifier.doCheckBase64(s[i])){
                    error(-100001,ctx,req.getSessionid());
                    continue;
                }
                 a += Messenger.RemoveOneMsg(clientInfo.etid, s[i]);
            }
            if(a >= s.length){
                //OK
                rep = new PongMsg(req.getSessionid());
                rep.setValue("ack",a+"");
                ctx.channel().writeAndFlush(rep);
            }else{
                error(-100003,ctx,req.getSessionid());
            }

        } catch (Exception e) {
            error(-100004,ctx,req.getSessionid());
        }
    }

    public static void Send(ChannelHandlerContext ctx, ClientInfo clientInfo, XLRequest req) {
        XLResponse rep = null;
        try {

            String type = "txt";
            if(req.getValues().containsKey("type")) {
                type = req.getValue("type");
            }

            String txt = req.getValue("t");
            Long target = Long.valueOf(req.getValue("o"));

            if(target <= Constant.MINIMAL_ETID && target >= Constant.MAX_ETID) {
                error(-100005,ctx,req.getSessionid());
                return;
            }

            // Verify的部分要求在prepare时实现
            //if(!Verifier.isValidB69(txt)){
            //    error(-100016,ctx,req.getSessionid());
            //    return;
            //}

            MessageBean msB;
            switch(type){
                case "rich":
                    msB = Messenger.PrepareRichMsg(clientInfo.etid, txt);
                    break;
                default:
                    msB = Messenger.PrepareTxtBase64Msg(clientInfo.etid, txt);
            }

            if(msB == null){
                error(-100006,ctx,req.getSessionid());
                return;
            }

            int s = Messenger.sendTextMsg(target, msB);

            if(s == 1000){
                rep = new SendAckMsg(req.getSessionid());
                ctx.writeAndFlush(rep);
            }else{
                error(-100007,ctx,req.getSessionid());
                return;
            }

        }catch (Exception e){
            error(-100008,ctx,req.getSessionid());
            return;
        }

    }

    public static void error(int i, ChannelHandlerContext ctx, int id) {
        XLResponse rep = new ExceptionalMsg(id,i);
        ctx.channel().writeAndFlush(rep);
    }

    public static void ConnectAgain(ChannelHandlerContext ctx, ClientInfo clientInfo, XLRequest req) {
        XLResponse rep = null;
        //收到客户端的请求
        if (Long.valueOf(req.getValue("e")) == clientInfo.etid) {
            // OK
            rep = new ExceptionalMsg(req.getSessionid());
            rep.setCommand(MsgType.CONNSUCCESS);
            rep.setResult(1000);

        } else {

            rep = new ExceptionalMsg(req.getSessionid());
            rep.setCommand(MsgType.CONNSUCCESS);
            rep.setResult(1000);
        }

        ctx.writeAndFlush(rep);
    }

    public static void reconnect(ChannelHandlerContext ctx, XLRequest req) {
        XLResponse rep = new XLResponse();
        rep.setCommand(MsgType.RECONNCET);
        rep.setSessionid(req.getSessionid());
        rep.setResult(-100009);
        ctx.writeAndFlush(rep);
    }

    public static void fetchMsg(ChannelHandlerContext ctx, ClientInfo clientInfo, XLRequest req) {
        try {
            String list = req.getValue("f");
            String[] m = list.split(",");
            String s = Messenger.getTxts(m);
            XLResponse rep = new MsgFetchRtn(req.getSessionid(),s);
            ctx.writeAndFlush(rep);
        }catch (Exception e){
            error(-100010,ctx,req.getSessionid());
        }
    }

    public static void pingAccept(ChannelHandlerContext ctx, ClientInfo clientInfo, XLRequest req) {
        XLResponse rep;
        try {
            rep = new PongMsg(req.getSessionid());
            rep.setResult(1000);
            ctx.writeAndFlush(rep);

            List<String> s = Messenger.getAllMessege(clientInfo.etid);
            if(s != null && !s.isEmpty()) {
                String ok = String.join(",",s);
                rep = new MsgPush();
                rep.setCommand(MsgType.MSGSYNC);
                rep.setValue("list", ok);
                ctx.writeAndFlush(rep);
            }
        } catch (Exception e) {
            error(-100011,ctx,req.getSessionid());
        }
    }

    public static void connect(ChannelHandlerContext ctx, ThreadLocal<ClientInfo> cli, XLRequest req) {
        XLResponse rep;
        long e = (new InnerLogin()).verify(req.getValue("e"), req.getValue("s"));
        if (Verifier.isValidEtid(e)) {
            // 判断是否存在另外的客户端，强制下线
            Channel x = NettyChannelMap.get(e);
            if(x != null && x.isActive()){
                x.close();
            }
            //登录成功,把channel存到服务端的map中
            NettyChannelMap.add(e, ctx.channel());
            cli.get().etid = e;
            log.info("用户 earth_" + e + " 登录成功");
            try {
                List<String> s = Messenger.getAllMessege(cli.get().etid);
                if(s == null || s.isEmpty()) {
                    rep = new ExceptionalMsg(req.getSessionid());
                    rep.setCommand(MsgType.CONNSUCCESS);
                    rep.setResult(1000);
                }else{
                    rep = new ExceptionalMsg(req.getSessionid());
                    rep.setCommand(MsgType.CONNSUCCESS);
                    rep.setResult(1000);
                    ctx.writeAndFlush(rep);

                    String ok = String.join(",",s);
                    rep = new MsgPush();
                    rep.setCommand(MsgType.MSGSYNC);
                    rep.setValue("list",ok);
                }
            } catch (Exception es) {
                es.printStackTrace();
                rep = new ExceptionalMsg(req.getSessionid());
                rep.setCommand(MsgType.CONNSUCCESS);
                rep.setResult(-100012);
            }
            ctx.channel().writeAndFlush(rep);
        } else {
            log.info("用户 earth_" + req.getValue("e") + " 的身份认证不通过");
            rep = new ExceptionalMsg(req.getSessionid());
            rep.setCommand(MsgType.RECONNCET);
            rep.setResult(-100013);
            ctx.writeAndFlush(rep);
            ctx.channel().close();
        }
    }
}
