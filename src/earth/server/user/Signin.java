package earth.server.user;

import earth.server.Constant;
import earth.server.utils.SHAUtil;

/**
 * Created by Frapo on 2017/1/22.
 */
public class Signin {
    private static final String EXTRA_CRED;// = "@(@(#()@(@(@(@(@(@#(#(#(#(#(#(#(#@(@(@(@(@(#(#(#(#(#(#(#(#@(@(@(@(@(@(@";

    public static String PasswordDigest(Long etid, String plain)
            throws Exception {
        if (etid <= Constant.MINIMAL_ETID) throw new Exception("2001");
        if (plain == null) throw new Exception("2003");
        return SHAUtil.SHA3Digest(256,etid.toString() + SHAUtil.SHA3Digest(512,plain + EXTRA_CRED));
    }

    static {
        EXTRA_CRED = copy("Earth", 100);
    }

    public static String copy(String str, int n) {
        String result = str;
        for (int i = 0; i < n; i++) {
            result = result.concat(str);
        }
        return result;
    }
}
