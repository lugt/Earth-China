package earth.Connection;

/**
 * Created by Frapo on 2017/2/1.
 * Version :01
 * Earth - Moudule earth.Connection
 */
public class MsgPush extends XLResponse{
    public MsgPush(){
        this.setEncode((byte)1);
        this.setCommand(MsgType.MSGARRIVE);
        this.setResult(1000);
        this.setSessionid(0);
    }

    public void setMsg(String msg){
        this.setValue("i",msg);
    }
}
