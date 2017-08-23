package earth.client.Long;

import java.util.concurrent.TimeUnit;
import earth.Connection.ConnectMsg;
import earth.Connection.PingMsg;
import earth.Connection.SendMsg;
import earth.Connection.XLRequest;
import earth.client.Util.Constant;
import earth.client.Util.Monitor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * Created by Frapo on 2017/1/22.
 */
public class LongClient {

    private static final String TAG = "longClient";
    private static boolean isLoggedin = false;
    private static int port;
    private static String host;

    private static ChannelFuture future;

    private static final EventExecutorGroup group = new DefaultEventExecutorGroup(20);

    private LongClient(int port, String host) throws InterruptedException {
        LongClient.port = port;
        LongClient.host = host;
        start();
    }

    public static boolean isConnected() {
        return (future!=null) && (future.channel().isActive()) && isLoggedin;
    }

    private static void start(){

        try {
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.group(eventLoopGroup);
            bootstrap.remoteAddress(host, port);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new XLClientEncoder());
                    socketChannel.pipeline().addLast(new XLClientDecoder());
                    socketChannel.pipeline().addLast(new LongClientHandler());
                    socketChannel.pipeline().addLast(new IdleStateHandler(100, 100, 0, TimeUnit.SECONDS));
                }
            });
            future = bootstrap.connect(host, port).sync();
            if (future.isSuccess()) {
                //socketChannel = (SocketChannel) future.channel();
                Monitor.logger("future to server  成功---------");
            }
        } catch (Exception e){
            // 链接失败了
            e.printStackTrace();
            earth.client.Util.Constant.getHandler().longStatus(-20001, 0,null);
        }
    }

    static int sessionCount = 1;

    public static long etid = 0L;
    public static String ssid = "0";

    public static LongClient getInstance(String host, int port) throws Exception {
        return new LongClient(port, host);
    }

    public static void connect(){
        ConnectMsg conn = new ConnectMsg(sessionCount ++, etid, ssid);
        if(future == null || !future.channel().isActive()){
            throw new IllegalStateException("Future is not active");
        }
        future.channel().write(conn);
        future.channel().flush();
    }

    public static void setLoggedin(boolean s){
        isLoggedin = s;
    }

    public static boolean init(String host, int port) {
        try {
            if(etid <= Constant.MINIMAL_ETID || etid >= Constant.MAX_ETID){
                return false;
            }
            if("0".equals(ssid)){
                return false;
            }
            if(future != null && future.channel() != null && future.channel().isActive()){
                return false;
            }
            getInstance(host,port);
            connect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void reconnect() {
        sessionCount = 1;
        Thread th = new Thread(new ReconnectTh());
        th.start();
    }

    public static int sendRequest(XLRequest req)  throws Exception{

        if(future == null || future.channel() == null ||!isLoggedin) return -20004;

        if(!future.channel().isActive()){
            LongClient.setLoggedin(false);
            return -20002;
        }

        future.channel().write(req);
        future.channel().flush();
        return 1000;
    }


    public static class ReconnectTh implements Runnable {

        private static long RECONNECT_SPAN = 5000L;

        @Override
        public void run() {
            try {
                Thread.sleep(RECONNECT_SPAN);
                LongClient.getInstance(host, port);
                LongClient.connect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e){
                // Recv 出现问题
                e.printStackTrace();
            }

        }
    }

    public static int getNewSessionCount(){
        sessionCount = sessionCount + 1;
        return sessionCount;
    }
}