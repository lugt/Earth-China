package earth.Connection;

/**
 * Created by God on 2017/2/8.
 */
public class MsgRcvAck extends XLRequest{
    public MsgRcvAck(int sessionId,String ids){
        this.setCommand(MsgType.MSGARRIVEACK);
        this.setSessionid(sessionId);
        this.setValue("list",ids);
    }
}
