package earth.server.space.facial;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

/**
 * Created by Frapo on 2017/2/5.
 * Version :16
 * Earth - Moudule earth.server.face
 */
public class FacialExecutor {

    public String getLinear(ByteBuf buf) {
        // get Pic from buf,
        // get Linear Customization
        return "SPFL,fail,except,1001";
    }

    public String getLandscapes(String path){
        String retn = null;
        try {
            retn = HttpUtil.postFileapi(HttpUtil.dtURL,path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retn;
    }
}
