package earth.Connection;

/**
 * Created by Frapo on 2017/2/5.
 * Version :21
 * Earth - Moudule earth.Connection
 */
public class SendMsg extends XLRequest{
    public SendMsg(int i, String msg, long l) {
        this.setCommand(MsgType.SEND);
        this.setValue("t", msg);
        this.setValue("o", String.valueOf(l));
    }
}
