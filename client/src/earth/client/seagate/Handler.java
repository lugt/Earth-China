package earth.client.seagate;

/**
 * Created by God on 2017/2/11.
 */
public class Handler {
    public Message obtainMessage(int what, Object s){
        Message msg =  new Message();
        msg.what = what;
        msg.obj = s;
        return msg;
    }

    public Message obtainMessage(int what){
        Message msg =  new Message();
        msg.what = what;
        return msg;
    }
}
