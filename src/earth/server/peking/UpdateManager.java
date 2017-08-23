package earth.server.peking;

import earth.server.Constant;
import earth.server.Monitor;
import earth.server.data.MsgEntity;
import earth.server.data.RedisConnect;
import io.netty.buffer.ByteBuf;
import org.hibernate.Session;

import java.nio.charset.Charset;

/**
 * Created by Frapo on 2017/2/5.
 * Version :16
 * Earth - Moudule earth.server.peking
 */
public class UpdateManager {

    public static String getUpdate (){
        return "U,ok,http://niimei.wicp.net/earth/earth-seagate.apk";
    }

    public static String setLog(ByteBuf log) {
        String m = log.toString(Charset.forName("UTF-8"));
        Long id = System.currentTimeMillis();
        try {
            MsgEntity msg = new MsgEntity();
            msg.setMsg(m);
            msg.setSender(1000);
            msg.setTarget(1000);
            msg.setStat((byte) 20);
            Session session = Constant.getSession();
            Constant.getTransact(session);
            session.save(msg);
        }catch (Exception e){
            Long k=System.currentTimeMillis();
            Monitor.logger("["+k+"] - Feedback-: " + e.getMessage());
            e.printStackTrace();
            return "Log,fail,except,"+k;
        }
        return "Log,ok";

    }
}
