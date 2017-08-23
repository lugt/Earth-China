package earth.Connection;

/**
 * Created by Frapo on 2017/1/24.
 */
public class ExceptionalMsg extends XLResponse {
    public ExceptionalMsg(int sessionid) {
        this.setSessionid(sessionid);
        this.setCommand(MsgType.EXCEPT);
    }

    public ExceptionalMsg(int sessionid, int s) {
        this.setSessionid(sessionid);
        this.setCommand(MsgType.EXCEPT);
        this.setResult(s);
    }
}
