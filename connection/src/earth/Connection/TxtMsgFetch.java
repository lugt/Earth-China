package earth.Connection;

/**
 * Created by God on 2017/2/8.
 */
public class TxtMsgFetch extends XLRequest{
    public TxtMsgFetch(int sessionId,String s){
        this.setSessionid(sessionId);
        this.setCommand(MsgType.FETCHMSG);
        this.setValue("f",s);
    }
}
