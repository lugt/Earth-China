package earth.client.seagate;

import earth.client.Util.Monitor;

/**
 * Created by God on 2017/2/11.
 */
public class Message {
    public int arg1;
    public int arg2;
    public int what;
    public Object obj;
    public void sendToTarget(){
        Monitor.logger("Sent to target what= "+what+" , args1=" + arg1 + " , args2="+arg2 + " , obj="+obj);
    }
}
