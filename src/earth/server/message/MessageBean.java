package earth.server.message;

import earth.server.utils.Verifier;
import javassist.bytecode.ByteArray;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Created by Frapo on 2017/1/31.
 * Version :17
 * Earth - Moudule earth.server.message
 */
public class MessageBean {

    public static final byte TXT = 1;
    public static final byte TXTCOMPLEX = 2; //复杂定义（包括Clearance/Encrypt/Burn/内部可以是其他等功能）
    //public static final int  = 3;

    public static final byte COMMONRICH = 61;
    public static final byte PAV = 62; // PIC/Audio/Video
    public static final byte FILE = 63; //Clearance/Encrypt

    public static final byte ZONE = 31; //ZONEACT
    public static final byte D3DMSG = 32; //D3D(Face Msg)

    public static final byte SYS = 91;
    public static final byte SVC = 92;
    public static final byte SUPER = 93; //需要Clearance
    // 8 bit = 1 byte  4bytes= 1int
    //        1             5         10             14           18
    // Stat : 接收 后 删除
    private byte type = 1; // 0-30 text 31-60 act/zone/3d 61-90 file/pic  90-120 System
    private int  id; // 4 byte
    private int  time; // 4 byte
    private int  len = 0; // 4 byte length

    private long etid ; // 8 byte - Who from
    public String m = "";

    /**
     * 设置消息
     * @param type = 1/31/61/91
     * @param ms 信息（包括文字、富文本、表情、空间消息、虚拟消息，请求）
     * */
    public void setMsg(byte type, byte[] ms){
        this.type = type;
        m = Base64.getMimeEncoder().encodeToString(ms);
        len = m.getBytes().length;
    }

    public void setInsecureBase64Msg(byte type, String m){
        this.type = type;
        this.m = m;
        len = m.getBytes().length;
    }

    @Override
    public String toString(){

        byte[] msg = m.getBytes();
        len = msg.length;

        byte[] byte_init = new byte[17];
        byte_init[0] = type;
        byte_init[1] = (byte) (0xff & len);
        byte_init[2] = (byte) ((0xff00 & len) >> 8);
        byte_init[3] = (byte) ((0xff0000 & len) >> 16);
        byte_init[4] = (byte) ((0xff000000 & len) >> 24);

        byte_init[5] = (byte) (0xff & time);
        byte_init[6] = (byte) ((0xff00 & time) >> 8);
        byte_init[7] = (byte) ((0xff0000 & time) >> 16);
        byte_init[8] = (byte) ((0xff000000 & time) >> 24);

        System.arraycopy(long2bytes(etid),0,byte_init,9,8);

        byte[] byte_et = Base64.getEncoder().encode(byte_init);
        byte[] out = new byte[len + byte_et.length];

        System.arraycopy(byte_et,0,out,0,byte_et.length);
        System.arraycopy(msg,0,out,byte_et.length,len);

        return new String(out);
    }

    public void setEtid(long etid) {
        this.etid = etid;
    }

    public void setId(int last) {
        id = last;
    }

    public void setTime(int times){
        time = times;
    }

    public static byte[] longToByte(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }
    public static byte[] long2bytes(long num) {
        byte[] b = new byte[8];
        for (int i=0;i<8;i++) {
            b[i] = (byte)(num>>>(56-(i*8)));
        }
        return b;
    }
    public static long bytes2long(byte[] b) {
        long temp = 0;
        long res = 0;
        for (int i=0;i<8;i++) {
            res <<= 8;
            temp = b[i] & 0xff;
            res |= temp;
        }
        return res;
    }

}
