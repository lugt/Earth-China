package earth.client.Short;

/**
 * Created by Frapo on 2017/1/22.
 * Version :16
 * Earth - Moudule ${PACKAGE_NAME}
 */
import earth.client.Util.Constant;
import earth.client.Util.Monitor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class ShortClient {

    //private static final int BUF_SIZE = 100 * 1024;
    private static String host = Constant.SHORTSERVER;
    private static int port = Constant.SHORTPORT;

    // TODO: Consider DNS Optimization
    public static void init(String shost, int shortport){
        host = shost;
        port = shortport;
    }

    public static int connect(URI uri,String msg) throws Exception {

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, false);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new ClientHandler());
                }
            });
            // Start the client.
            
            ChannelFuture f = b.connect(host, port).sync();
            DefaultFullHttpRequest request = null;
                        request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                    uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));
            // 构建http请求
            request.headers().set(HttpHeaderNames.HOST, host);
            //request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaders.Values.);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());

            // 发送http请求
            f.channel().write(request);
            f.channel().flush();
            return 1000;
        } catch (InterruptedException | UnsupportedEncodingException e ) {
            e.printStackTrace();
            return -5001;
        }catch (Exception e){
            e.printStackTrace();
            return -5002;
        } /**
            finally {
            workerGroup.shutdownGracefully();
            return 1000;
        }*/
    }
    public static int getKiosk(){
        URI uri = getUri("kiosk/aq");
        String msg = "halso,@#$!%";
        return nettySend(uri,msg);
    }
    public static int getTest(){
        URI uri = getUri("test");
        String msg = "halso,@#$!%";
        return nettySend(uri,msg);
    }
    public static int syncLogin(String phone,String password){
        URI uri = getUri("sign/phone");
        String msg = phone+","+password+",kiosk,"+((Double) (Math.random()*10000)).intValue();
        return nettySend(uri,msg);
    }
    public static int syncReg(String phone,String password){
        URI uri = getUri("reg");
        String msg = phone+","+password+",kiosk,"+((Double) (Math.random()*10000)).intValue();
        return nettySend(uri,msg);
    }
    public static int establish(){
        URI uri = getUri("welcome");
        String msg = Constant.ClientV+","+Constant.ProtoV;
        return nettySend(uri,msg);

    }

    private static URI getUri(String s) {
        try {
            return new URI("/"+s);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static URL getUrl(String s) {
        try {
            return new URL("https://"+host+":"+port+s);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private  static int nettySend(final URI uri,final String msg) {
        new Thread(){
            public void run(){
                httpsSend(getUrl(uri.getPath()),msg);
            }
        }.start();
        return 1000;
        /*
        try {
            return connect(uri,msg);
        } catch (Exception e) {
            e.printStackTrace();
            return -10002;
        }
        */
    }

    private static int httpsSend(URL url,String msg){
        try {
            String u = smallHttpsPost(url,msg.getBytes());
            if(u == null || u.length() <= 0) return -5002;
            Constant.getHandler().shortReturn(u,null);
            return 1000;
        } catch (IOException e) {
            e.printStackTrace();
            return -5002;
        } catch (Exception e) {
            e.printStackTrace();
            Monitor.logger("Except");
            return -5002;
        }
    }

    private static String smallHttpPost(URL url, byte[] data) throws Exception {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) ...");
        con.setRequestProperty("Accept-Language", "zh-CN,en;q=0.5");
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        if(data != null && data.length > 0) {
            con.setDoInput(true);
            OutputStream out = con.getOutputStream();
            out.write(data);
        }
        con.connect();
        //Object objs = con.getContent();

        // 取得该连接的输入流，以读取响应内容
        InputStreamReader insr = new InputStreamReader(con.getInputStream());
        // 读取服务器的响应内容并显示
        int respInt = insr.read();
        //char[] buf = new char[BUF_SIZE];     //int i = 0;
        /* i++;
        if(i >= BUF_SIZE){
            throw new IOException("buf not enough");
        }*/
        StringBuffer sb = new StringBuffer();
        while (respInt != -1) {
            //buf[i] = (char) respInt;
            sb.append((char) respInt);
            respInt = insr.read();
        }
        Monitor.response(sb.toString());
        return sb.toString();

    }

    private static SSLSocketFactory sf;

    static {
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new EarthTrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            sf = sslContext.getSocketFactory();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    private static String smallHttpsPost(URL obj,byte[] data) throws IOException, Exception{

        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) ...");
        con.setRequestProperty("Accept-Language", "zh-CN,en;q=0.5");
        con.setRequestMethod("POST");
        con.setSSLSocketFactory(sf);
        con.setDoOutput(true);
        if(data != null && data.length > 0) {
            con.setDoInput(true);
            OutputStream out = con.getOutputStream();
            out.write(data);
        }
        con.connect();
        //Object objs = con.getContent();

        // 取得该连接的输入流，以读取响应内容
        InputStreamReader insr = new InputStreamReader(con.getInputStream());
        // 读取服务器的响应内容并显示
        int respInt = insr.read();
        //char[] buf = new char[BUF_SIZE];     //int i = 0;
        /* i++;
        if(i >= BUF_SIZE){
            throw new IOException("buf not enough");
        }*/
        StringBuffer sb = new StringBuffer();
        while (respInt != -1) {
            //buf[i] = (char) respInt;
            sb.append((char) respInt);
            respInt = insr.read();
        }
        Monitor.response(sb.toString());
        return sb.toString();
    }
    public static int syncFriend(String ssid) {
        URI uri = getUri("friend/list");
        if(ssid.length() == 19) {
            String msg = ssid + "," + Math.random();
            return nettySend(uri, msg);
        }else{
            return -10001;
        }
    }
    public static int addFriend(String ssid, String target) {
        URI uri = getUri("friend/add");
        if(ssid.length() == 19) {
            String msg = ssid + "," + target + "," + Math.random();
            return nettySend(uri, msg);
        }else{
            return -10002;
        }
    }
    public static int rmFriend(String ssid, String target) {
        URI uri = getUri("friend/remove");
        if(ssid.length() == 19) {
            String msg = ssid + "," + target + "," + Math.random();
            return nettySend(uri, msg);
        }else{
            return -10002;
        }
    }


}