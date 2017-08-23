package earth.Connection;

/**
 * Created by Frapo on 2017/1/24.
 * Version :01
 * Earth - Moudule ${PACKAGE_NAME}
 */
public class ConnectMsg extends XLRequest {
    public ConnectMsg(int sessionId, long l, String sess) {
        this.setCommand(MsgType.CONNCET);
        this.setSessionid(sessionId);
        this.setValue("e", Long.toString(l));
        this.setValue("s", sess);
    }
}
