package earth.server.message;
import earth.Connection.MsgPush;
import earth.Connection.XLResponse;
import earth.server.Monitor;
import earth.server.data.RedisConnect;
import earth.server.tianjin.Server.NettyChannelMap;
import earth.server.utils.Verifier;

import io.netty.channel.Channel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * Created by Frapo on 2017/1/31.
 * Version :16
 * Earth - Moudule earth.server.message
 */
public class Messenger {

    private static void getTransact(Session session){
        if(session.getTransaction() == null){
            session.beginTransaction().setTimeout(3);
        }else {
            if(!session.getTransaction().isActive()){
                session.beginTransaction();
            }
        }
    }

    private static Log log = LogFactory.getLog(Messenger.class);



    public static int sendTextMsg(Long target, MessageBean msB) throws Exception{

        String t = Base64.getEncoder().withoutPadding().encodeToString(MessageBean.longToByte(System.currentTimeMillis()));
        RedisConnect connect = getRedisConnect(0);
        connect.setValue(t, msB.toString());
        connect.close();
        // 加入对方的Sync队列
        connect = getRedisConnect(1);
        long len = connect.rpush(target+"_",t);
        if(len <= 0L){
            // Error;
            Monitor.error("Error : -1000020 target=" + target + ", id="+t);
            return -100020;
        }
        connect.close();
        if(NettyChannelMap.get(target) != null){
            immediate_Send(target, t, msB);
        }
        return 1000;
    }

    private static void immediate_Send(Long target, String t, MessageBean msB) {
        // Instant Call to send the msB to the client
        // 查找NettyClientMap，etid
        Channel ch = NettyChannelMap.get(target);
        if(ch != null && ch.isActive()){
            XLResponse rep = new MsgPush();
            rep.setValue("id", t);
            rep.setValue("m", msB.toString());
            ch.writeAndFlush(rep);
        }
    }

    private static RedisConnect getRedisConnect(int db) throws Exception {
        return new RedisConnect(db);
    }

    public static MessageBean PrepareTxtMsg(long from, String txt){
        try{
            MessageBean msB = new MessageBean();
            Long time = System.currentTimeMillis();
            time = time/1000L;
            msB.setTime(time.intValue());
            msB.setMsg(MessageBean.TXT, txt.getBytes());
            msB.setEtid(from);
            return msB;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e);
            return null;
        }
    }


    public static MessageBean PrepareRichMsg(long from, String token){
        try{
            if(!Verifier.isValidB69(token)) return null;
            MessageBean msB = new MessageBean();
            Long time = System.currentTimeMillis();
            time = time/1000L;
            msB.setMsg(MessageBean.COMMONRICH, token.getBytes());
            msB.setTime(time.intValue());
            msB.setEtid(from);
            return msB;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e);
            return null;
        }
    }

    public static int sendRichMsg(Long target, MessageBean msB) throws Exception{

        String t = Base64.getEncoder().withoutPadding().encodeToString(MessageBean.longToByte(System.currentTimeMillis()));
        RedisConnect connect = getRedisConnect(0);
        connect.setValue(t, msB.toString());
        connect.close();
        // 加入对方的Sync队列
        connect = getRedisConnect(1);
        long len = connect.rpush(target + "_", t);
        if(len <= 0L){
            // Error;
            Monitor.error("Error : -1000019 target=" + target + ", id="+t);
            return -100019;
        }
        connect.close();
        if(NettyChannelMap.get(target) != null){
            immediate_Send(target,t,msB);
        }
        return 1000;
    }


    public static List<String> getAllMessege(Long me) throws Exception{
        RedisConnect connect = getRedisConnect(1);
        List<String> all = connect.lstart(me + "_", 0, -1);
        connect.close();
        return all;
    }

    public static String popMessage(long l) throws Exception{
        RedisConnect connect = getRedisConnect(1);
        String m = connect.lpop(l + "_");
        connect.close();
        return m;
    }

    public static long RemoveOneMsg(long etid, String mId) throws Exception{

        RedisConnect connect = getRedisConnect(1);
        Long m = connect.lrem(etid + "_", 1L, mId);
        connect.close();
        if(m < 1L){
            return m;
        }
        connect = getRedisConnect(0);
        m = connect.remove(mId);
        connect.close();
        return m;

    }

    public static MessageBean PrepareTxtBase64Msg(long from, String txt) {
        try{
            if(!Verifier.doCheckBase64(txt)) return null;
            MessageBean msB = new MessageBean();
            Long time = System.currentTimeMillis();
            time = time/1000L;
            msB.setTime(time.intValue());
            msB.setInsecureBase64Msg(MessageBean.TXT, txt);
            msB.setEtid(from);
            return msB;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e);
            return null;
        }
    }

    public static String getTxt(String s) throws Exception{
        RedisConnect connect = getRedisConnect(0);
        String a = connect.getValue(s);
        connect.close();
        if(a == null || Objects.equals(a, "null")) a = "e";
        return a;
    }

    public static String getTxts(String[] m) throws Exception{
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m.length; i++) {
            sb.append(getTxt(m[i]));
            sb.append(",");
            sb.append(m[i]);
            if(i < m.length - 1) sb.append(",");
        }
        return  sb.toString();
    }
}
