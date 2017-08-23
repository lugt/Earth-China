package earth.Connection;

/**
 * Created by Frapo on 2017/2/1.
 * Version :12
 * Earth - Moudule earth.Connection
 */
public class SendAckMsg extends XLResponse {
    public SendAckMsg(int sessionid) {
        super();
        this.setSessionid(sessionid);
        this.setCommand(MsgType.SENDACK);
        setResult(1000);
    }
}
