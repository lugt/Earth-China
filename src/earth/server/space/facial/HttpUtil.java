package earth.server.space.facial;

import earth.server.Monitor;
import earth.server.Nanjing.FileServer;
import earth.server.user.Signin;
import org.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Base64;


/**
 * Created by God on 2017/2/11.
 */
public class HttpUtil {
    public static final String dtURL="https://api-cn.faceplusplus.com/facepp/v3/detect";

    /**
     *
     face_tokens  String 逗号分隔
     return_landmark   Int  [0,1]
     return_attributes String  [gender, age, smiling, glass, headpose,facequality,blur]
     一个字符串，由一个或多个人脸标识组成，用逗号分隔。最多支持5个face_token。
     是否检测并返回根据人脸特征判断出的年龄，性别，微笑、人脸质量等属性，需要将需要检测的属性组织成一个用逗号分隔的字符串。目前支持：gender, age, smiling, glass, headpose,facequality,blur 顺序没有要求。默认值为 none ，表示不检测属性。请注意none如果与任何属性共用则都不检测属性。
     注：facequality（人脸质量）是指图像中的人脸是否适合进行人脸比对，出现模糊、过亮、过暗、大侧脸、不完整等情况会影响人脸质量分数。

     @return request_id	String	用于区分每一次请求的唯一的字符串。
     faces  Array
     time_used  Int  整个请求所花费的时间，单位为毫秒。
     error_message   String   当请求失败时才会返回此字符串，具体返回内容见后续错误信息章节。否则此字段不存在

     face_token   String  人脸的标识
     face_rectangle  Object  人脸矩形框，坐标数字为整数，代表像素点坐标

     top：左上角纵坐标
     left：左上角横坐标
     width：宽度
     height：高度
     landmark     Object     人脸的83个关键点坐标数组。
     attributes     Object     人脸属性特征，包括：
     * */
    public static final String alURL="https://api-cn.faceplusplus.com/facepp/v3/face/analyze";
    public static final String hsURL="https://api-cn.faceplusplus.com/humanbodypp/beta/segment";
    private static final int BUF_SIZE = 100000;


    public static String basicHttpsPost(String uri,byte[] data) throws IOException, KeyManagementException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, KeyStoreException {

        URL obj = new URL(uri);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
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
        Object objs = con.getContent();

        // 取得该连接的输入流，以读取响应内容
        InputStreamReader insr = new InputStreamReader(con.getInputStream());
        // 读取服务器的响应内容并显示
        int respInt = insr.read();
        char[] buf = new char[BUF_SIZE];
        int i = 0;
        while (respInt != -1) {
            buf[i] = (char) respInt;
            System.out.print((char) respInt);
            respInt = insr.read();
            i++;
            if(i >= BUF_SIZE){
                throw new IOException("buf too huge");
            }
        }
        String outs = String.valueOf(buf);
        String response = String.valueOf(respInt);
        Monitor.response(response);
        return response;
    }


    public static String getUrl(String uri) throws Exception{

        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager[] tm = { new EarthTrustManager() };
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();

        // 创建URL对象
        URL myURL = new URL(uri);
        // 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
        HttpsURLConnection httpsConn = (HttpsURLConnection) myURL.openConnection();
        httpsConn.setSSLSocketFactory(ssf);

        // 取得该连接的输入流，以读取响应内容
        InputStreamReader insr = new InputStreamReader(httpsConn
                .getInputStream());
        // 读取服务器的响应内容并显示
        int respInt = insr.read();
        char[] buf = new char[BUF_SIZE];
        int i = 0;
        while (respInt != -1) {
            buf[i] = (char) respInt;
            //System.out.print((char) respInt);
            respInt = insr.read();
            i++;
            if(i >= BUF_SIZE){
                throw new IOException("buf too huge");
            }
        }

        String response = String.valueOf(buf);
        Monitor.response(response);
        return response;
    }

    public static void main(String[] args){
        try {
            //System.out.print(basicHttpsPost(dtURL,null));
            //String retn = getUrl("https://niimei.wicp.net:7999/");
            //String retn = "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";

            String retn = humanShapeApi(hsURL,"C:\\Users\\God\\Desktop\\aaa.jpg");
            System.out.println(retn);
            JSONObject obj = new JSONObject(retn);
            System.out.println(obj.getString("result").length());
            byte[] buffer = java.util.Base64.getDecoder().decode(obj.getString("result").getBytes());
            Monitor.logger(buffer.length);
            for (int i = 0; i < 122; i++) {
                for (int j = 0; j < 179; j++) {
                    float foo = Float.intBitsToFloat(buffer[(i*122+j)*4] ^ buffer[(i*122+j)*4 + 1] << 8 ^ buffer[(i*122+j)*4 + 2] << 16 ^ buffer[(i*122+j)*4 + 3] << 24);
                    System.out.printf("%.0f",foo);
                    System.out.print(",");
                }
                System.out.println();
            }
            System.out.println(retn);
            Monitor.logger(Signin.PasswordDigest(1003L,"MTIz"));
            //FileServer.newBuild().start();
            while (true){
                Thread.sleep(200L);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String postFileapi(String uri,String filepath) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(uri);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("api_key", "GXl0r4bT-7I5Z2wQ-15Srlc29xh9H0vt", ContentType.TEXT_PLAIN);
        builder.addTextBody("api_secret","ohYav-_fjbBxCyfyGBH9W3-ZkRzTSWJ9", ContentType.TEXT_PLAIN);
        builder.addTextBody("return_landmark","1", ContentType.TEXT_PLAIN);
        builder.addTextBody("return_attributes","gender,age,smiling,glass,headpose,facequality,blur", ContentType.TEXT_PLAIN);
// This attaches the file to the POST:
        File f = new File(filepath);
        builder.addBinaryBody(
                "image_file",
                new FileInputStream(f),
                ContentType.APPLICATION_OCTET_STREAM,
                f.getName()
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        HttpEntity responseEntity = response.getEntity();
        ByteArrayOutputStream outS = new ByteArrayOutputStream();
        responseEntity.writeTo(outS);
        return new String(outS.toByteArray());
    }

    public static String humanShapeApi(String uri,String filepath) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(uri);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("api_key", "GXl0r4bT-7I5Z2wQ-15Srlc29xh9H0vt", ContentType.TEXT_PLAIN);
        builder.addTextBody("api_secret","ohYav-_fjbBxCyfyGBH9W3-ZkRzTSWJ9", ContentType.TEXT_PLAIN);
        File f = new File(filepath);
        builder.addBinaryBody(
                "image_file",
                new FileInputStream(f),
                ContentType.APPLICATION_OCTET_STREAM,
                f.getName()
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        HttpEntity responseEntity = response.getEntity();
        ByteArrayOutputStream outS = new ByteArrayOutputStream();
        responseEntity.writeTo(outS);
        return new String(outS.toByteArray());
    }
}
