package earth.client.Short;

/**
 * Created by Frapo on 2017/1/31.
 * Earth 20:19
 */

public interface ShortHandlerInterface {
    void longStatus(int status,int result,Object obj);
    void longReturn(int command,int result,Object obj);
    void shortReturn(String response,Object obj);
    void shortStatus(int status,String val,Object obj);
}
