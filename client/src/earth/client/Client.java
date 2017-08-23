package earth.client;

/**
 * Created by Frapo on 2017/1/29.
 * Version :00
 * Earth - Moudule earth.client
 */

import earth.Connection.PingMsg;
import earth.Connection.SendMsg;
import earth.client.Long.LongClient;
import earth.client.Short.ShortClient;
import earth.client.Util.Constant;
import earth.client.Util.Monitor;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frapo on 2017/1/29.
 * Version :00
 * Earth - Moudule earth.client
 */

import earth.Connection.SendMsg;

import earth.client.Long.LongClient;
import earth.client.Short.ShortClient;

import earth.client.Util.Constant;
import earth.client.Util.Monitor;
import earth.client.seagate.Handler;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class Client {

    public static void main(String[] args) {

        LocalHandler handler = new LocalHandler();
        LocalHandler.setHandler(new Handler());
        Constant.setHandler(handler);

        try {

            login();
            TimeUnit.SECONDS.sleep(5L);
            /*int i = getFriend();
            TimeUnit.SECONDS.sleep(2L);
            i = addFriend();
            TimeUnit.SECONDS.sleep(2L);
            i = rmFriend();
            TimeUnit.SECONDS.sleep(5L);
            */
            LocalHandler.initLong();
            TimeUnit.SECONDS.sleep(3L);
            if(LongClient.isConnected()) {
                //int i = sendMsg();
                //Monitor.logger(i);
            }else{
                LongClient.connect();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            TimeUnit.SECONDS.sleep(5L);
            PingMsg ping = new PingMsg(LongClient.getNewSessionCount());
            LongClient.sendRequest(ping);
            TimeUnit.SECONDS.sleep(5L);
            //int i = sendMsg();
            //Monitor.logger(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            int i = 0;
            e.printStackTrace();
        }


        while(true){
            return;
        }
    }

    private static int sendMsg() {
        try {
            String out = Base64.getEncoder().encodeToString("Hello! This is me Speaking! I like your style \n \n \n \r\n HaHaHahahahahahahh".getBytes());
            SendMsg send = new SendMsg(LongClient.getNewSessionCount(), out,2017L);
            int i = 0;
            if((i = LongClient.sendRequest(send)) != 1000){
                // 异常
                Monitor.error("Exception: not 1000 on sending msg");
                return i;
            }
            return 1000;
        } catch (Exception e) {
            e.printStackTrace();
            return -3002;
        }
    }

    private static int addFriend() {
        return ShortClient.addFriend(LongClient.ssid,String.valueOf(LongClient.etid));
    }

    private static int rmFriend() {
        return ShortClient.rmFriend(LongClient.ssid, String.valueOf(LongClient.etid));
    }

    public static void login() {
        //ShortClient.syncLogin("18611823550", "29a2");
        ShortClient.syncLogin("13100000000", "MTIz");
    }

    public static int getFriend() {
        return ShortClient.syncFriend(LongClient.ssid);
    }
}

