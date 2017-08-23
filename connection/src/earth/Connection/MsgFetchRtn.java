package earth.Connection;

/**
 * Created by Frapo on 2017/2/5.
 * Version :12
 * Earth - Moudule earth.Connection
 */
public class MsgFetchRtn extends XLResponse {
    public MsgFetchRtn(int sessionId,String rtn){
        // rtn
        this.setCommand(MsgType.FETCHMSGRTN);
        this.setResult(1000);
        this.setSessionid(sessionId);
        this.setValue("list",rtn);
    }
}
