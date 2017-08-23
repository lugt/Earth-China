package earth.client.Util;

import earth.client.Short.ShortHandlerInterface;

/**
 * Created by Frapo on 2017/1/22.
 */
public class Constant {


    public static final String LONG_SERVER = "niimei.wicp.net"; //"121.42.198.57";
    public static final String SHORTSERVER = LONG_SERVER;
    public static final int SHORTPORT = 7999;
    public static final int LONGPORT = 9999;

    public static final String ServerV = "1005";
    public static final String ClientV = "1005";
    public static final String ProtoV = "1005";
    public static final String MinimunProto = "1005";
    public static final String SecureEnforce = "plain";
    public static final long MINIMAL_ETID = 998L;
    public static final Long MAX_ETID = 6999L;

    private static ShortHandlerInterface handler;
    public static void setHandler(ShortHandlerInterface handle){
        handler = handle;
    }
    public static ShortHandlerInterface getHandler(){
        return handler;
    }

}
