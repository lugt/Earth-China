package earth.client;
/**
 * Created by Frapo on 2017/1/29.
 * Version :00
 * Earth - Moudule earth.client
 */

import earth.Connection.*;
import earth.client.Long.LongClient;
import earth.client.Short.ShortHandlerInterface;
import earth.client.Util.Constant;
import earth.client.Util.MessageBean;
import earth.client.Util.Monitor;
import earth.client.seagate.Handler;
import earth.client.seagate.Message;

import java.util.Arrays;

public class LocalHandler implements ShortHandlerInterface {

    static Handler hdl = null;
    public static void setHandler(Handler h){
        hdl = h;
    }

    @Override
    public void shortReturn(String http,Object obj) {
        String[] m = http.split(",");
        if(m.length > 1) {
            switch(m[0]) {
                case "FrL":
                    friendList(m);
                    break;
                case "Frm":
                case "FAdd":
                    friendAdd(m);
                    break;
                case "Auto":
                    loginReturn(m);
                    break;
                case "L":
                    loginReturn(m);
                    break;
                case "W":
                    checkServerAvailability(m);
                    break;
                case "C":
                    regReturn(m);
                    break;
                default:
                    unHandled(m);
            }
        }else{
            unUsual(m);
        }

    }

    private static void checkServerAvailability(String[] m) {
        if(Constant.ClientV.equals(m[1])){
            // 最新版本，无需更新
        }else{
            // 判断相关版本更新
            hdl.obtainMessage(-9999,"update").sendToTarget();
        }
    }

    private void friendAdd(String[] m) {
        if("ok".equals(m[1])){
            hdl.obtainMessage(2501).sendToTarget();
        }else{
            hdl.obtainMessage(-201).sendToTarget();
        }
    }

    private void friendList(String[] m) {
        if("ok".equals(m[1])){
            hdl.obtainMessage(2502,m[2]).sendToTarget();
        }else{
            hdl.obtainMessage(-202).sendToTarget();
        }
    }

    private void unUsual(String[] m) {
        Monitor.logger("Unusual Signal Rcvd : " + Arrays.toString(m));
    }

    private void loginReturn(String[] m) {
        if("ok".equals(m[1])) {
            Monitor.logger("Successfully logged the account , ssid : " + m[2] + "-- etid : " + m[3]);
            LongClient.ssid = m[2];
            LongClient.etid = Long.valueOf(m[3]);
            hdl.obtainMessage(1000,"login").sendToTarget();
        } else {
            hdl.obtainMessage(-20,m).sendToTarget();
            Monitor.logger("Error : Login failed - " + m[2] + "," + (m.length >= 4?m[3]:""));
        }

    }

    private void regReturn(String[] m) {
        if("ok".equals(m[1])) {
            Monitor.logger("Successfully registered account , etid : " + m[2]);
            hdl.obtainMessage(1000,"reg").sendToTarget();
        } else {
            hdl.obtainMessage(-3200,m).sendToTarget();
            Monitor.logger("Error : Register failed - " + m[2] + "," + (m.length >= 4?m[3]:""));
        }

    }

    private void unHandled(String[] m) {
        Monitor.logger("earth.client.Client Rcvd : " + m[0]+","+m[1]);
        hdl.obtainMessage(-40).sendToTarget();
    }


    @Override
    public void longStatus(int status, int result, Object obj) {
        // 判断Result是啥，stauts是啥
        if(status == -20006) {
            Monitor.logger("成功登陆长连接服务");
            hdl.obtainMessage(1000,"long").sendToTarget();
        }else if(status == -20003 || status == -20002 || status == -20004 || status == -20007  ) {
            hdl.obtainMessage(-150).sendToTarget();
        }else{
            hdl.obtainMessage(status).sendToTarget();
            earth.client.Util.Monitor.logger(status + ":" + result + "   " + obj);
        }
    }

    @Override
    public void longReturn(int command, int result, Object obj) {
        XLResponse rep = new XLResponse();
        if(obj instanceof XLResponse){
            rep = (XLResponse) obj;
        }
        switch (command){
            case MsgType.MSGSYNC:{
                String out = rep.getValue("list");
                msgsyncget(out);
            }break;
            case MsgType.FETCHMSGRTN:{
                fetchmsgrtn(rep.getValue("list"));
            }break;
            case MsgType.MSGARRIVE:{
                msgarrive(rep.getValue("id"), rep.getValue("m"));
            }break;
            case MsgType.SENDACK:{
                sendack(rep.getResult(),rep.getSessionid());
            }
            case MsgType.EXCEPT: {
                // f服务器/客户端出现了一些问题
                except(rep);
            }break;
            default:
                unexpectedHandler(rep);
        }
    }

    /**
     * 发送消息的结果
     * */
    private void sendack(int result, int sessionid) {
        // 处理消息发送成功/失败
        if(result == 1000) {
            Monitor.logger("发送的消息已经记录 : [" + sessionid + "] - " + result);
            Message msg = hdl.obtainMessage(3410);
            msg.arg1 = sessionid;
            msg.sendToTarget();
        }else {
            hdl.obtainMessage(-3412).sendToTarget();
        }
    }

    /**
     * 收到默认的消息推送（可能有多组）
     * 消息格式 消息id1,消息id2,消息id3,消息id4, ... ,消息idn
     * 处理对策： 将发来的消息记录，发送fetch获取消息体的请求，根据id获取消息体
     * */
    private void msgsyncget(String out) {
        // 统一fetch
        try {
            TxtMsgFetch fetch = new TxtMsgFetch(LongClient.getNewSessionCount(), out);
            int s = LongClient.sendRequest(fetch);
            if(s != 1000){
                Monitor.error("发送获取消息体的请求失败." + s);
            }
        }catch (Exception e){
            //错误处理
            e.printStackTrace();
        }
    }

    private void except(XLResponse rep) {
        // get
        String t = rep.getValue("e");
        switch(rep.getResult()) {
            case -100001:
                break;
            case -100002:
                break;
            case -100003:
                break;
            case -100005:
                break;
            case -100006:
                break;
            case -100007:
            case -100004:
            case -100008:
            case -100011:
            case -100012:
                // 服务器的异常
                break;
            case -100009:
                break;
            case -100010:
                break;
            case -100013:
                break;
            case -100014:
                break;
            case -100015:
                break;
            case -100016:
                break;
            case -100017:
                break;
            case -100018:
                // 关闭了连接
                break;
            case -100019:
                break;
            case -100020:
                break;
            case -100021:
                break;
            case -200010:
                break;
            default:
        }
    }

    private void msgarrive(String id, String m) {
        try {
            int o = 0;
            if(id == null || m == null || "e".equals(m)){
                // 消息出错
                Monitor.logger("获取即时消息出错。id = "+id+"  m = "+m);
            }else if(1000 == (o = receiveOneMsg(m))){
                XLRequest req = new MsgRcvAck(LongClient.getNewSessionCount(),id);
                int s = LongClient.sendRequest(req);
                if(s != 1000){
                    Monitor.logger("新消息处理成功，但发送成功Ack失败 " + s);
                }
            }else{
                Monitor.logger("新消息处理失败 " + o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理获取的多组消息
     * 消息格式 消息体1,消息体2,消息体3,...,消息体n
     * 通过new MessageBean来还原消息
     * */
    private void fetchmsgrtn(String a) {
        try {
            String[] q = a.split(",");
            StringBuilder sb = new StringBuilder();
            int o;
            for (int i = 0; i < q.length; i+=2) {
                if("e".equals(q[i])) {
                    // 服务器返回的消息体是空的，需要重新获取
                }else if(1000 == (o=receiveOneMsg(q[i])) ){
                    // OK 处理成功
                    sb.append(q[i+1]).append(",");
                }else{
                    // receiveOneMsg 返回了错误
                    Monitor.logger("本地处理消息体失败 = "+o);
                }
            }

            if(sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
                XLRequest req = new MsgRcvAck(LongClient.getNewSessionCount(), sb.toString());
                int s = LongClient.sendRequest(req);
                if (s != 1000) {
                    // 出错了
                    Monitor.error("批量消息处理成功，但是发送批量成功Ack失败 : " + s);
                }
            }else{
                // 批量消息处理的全部失败了
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initLong() {
        new Thread(){
            @Override
            public void run(){
                if(!LongClient.init(Constant.LONG_SERVER,Constant.LONGPORT)){
                    hdl.obtainMessage(-100,"long").sendToTarget();
                }
            }
        }.start();
    }

    private int receiveOneMsg(String s) {
        try {
            MessageBean msB = new MessageBean(s);
            // Call UI Thread...
            hdl.obtainMessage(2000,msB).sendToTarget();
            // Add to Local Storage
            /// Handling
            Monitor.error(msB.getSender() + " said : " + msB.msg);
            // 回复1000为成功，其他为失败
            return 1000;
        } catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().equals("HeadNotFitException")){
                return -3003;
            }
            return -3000;
        }
    }

    private void unexpectedHandler(XLResponse rep) {
        // Logger
        Monitor.logger("意外的 long rcvd : " + rep.toString() + "RESPONSE VALUES" + Arrays.toString(rep.getValues().entrySet().toArray()));
    }

    @Override
    public void shortStatus(int status, String val, Object obj) {
        Monitor.logger("短连接服务出现status返回： error = " + status);
    }

    public static void innerCall(int i, String s) {
        hdl.obtainMessage(i,s).sendToTarget();
    }
}
