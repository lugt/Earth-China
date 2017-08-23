package earth.client.Util;

/**
 * Created by God on 2017/2/11.
 */
public class Base64 {

    public static final int NO_WRAP = 0;

    public static byte[] decode(byte[] p, int flag) {
        return java.util.Base64.getDecoder().decode(p);
    }

    public static String encodeToString(byte[] ms, int flag) {
        return java.util.Base64.getEncoder().encodeToString(ms);
    }
}
