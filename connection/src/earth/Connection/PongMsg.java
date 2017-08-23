package earth.Connection;

/**
 * Created by Frapo on 2017/1/24.
 */
public class PongMsg extends XLResponse {
    public PongMsg(int session) {
        this.setSessionid(session);
        this.setCommand(MsgType.PONG);
    }
}
