package earth.Connection;

/**
 * Created by Frapo on 2017/1/24.
 */
public class PingMsg extends XLRequest {
    public PingMsg(int session) {
        this.setSessionid(session);
        this.setCommand(MsgType.PING);
    }
}
