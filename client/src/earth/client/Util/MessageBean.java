package earth.client.Util;

import java.nio.charset.Charset;

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
    private int  time; // 4 byte
    private int  len = 0; // 4 byte length

    private long etid ; // 8 byte - Who from
    public String msg = null;

    public MessageBean(String s) throws Exception{
        if(s == null){
            throw new IllegalStateException("Empty Message to Bean Exception");
        }
        byte[] f = s.getBytes();
        byte[] p = new byte[24];
        byte[] head;

        System.arraycopy(f,0,p,0,24);
        head = Base64.decode(p,Base64.NO_WRAP);

        if(head.length != 17){
            throw new Exception("HeadNotFitException");
        }

        type = head[0];
        len = byteArrayToInt(head,1);
        time = byteArrayToInt(head,5);
        etid = bytes2long(head,9);
        p = new byte[f.length - 24];
        System.arraycopy(f,24,p,0,f.length - 24);
        if(f.length - 24 == len){
            // Corrupted
            this.msg = new String(Base64.decode(p,Base64.NO_WRAP), Charset.forName("utf8"));
        }else{
            throw new Exception("LengthCheckFailed");
        }
    }

    public MessageBean(long etid, String msg, int type,int time) {
        this.type = (byte) type;
        this.etid = etid;
        this.time = time;
        this.msg = msg;
    }

    /**
     * 设置消息
     * @param type = 1/31/61/91
     * @param ms 信息（包括文字、富文本、表情、空间消息、虚拟消息，请求）
     * */
    public void setMsg(byte type, byte[] ms){
        this.type = type;
        msg = Base64.encodeToString(ms,Base64.NO_WRAP);
        len = msg.getBytes().length;
    }

    public static byte[] long2bytes(long num) {
        byte[] b = new byte[8];
        for (int i=0;i<8;i++) {
            b[i] = (byte)(num>>>(56-(i*8)));
        }
        return b;
    }

    //byte 数组与 int 的相互转换
    private static int byteArrayToInt(byte[] b, int startpos) throws Exception{
        return   b[startpos] & 0xFF |
                (b[startpos + 1] & 0xFF) << 8 |
                (b[startpos + 2] & 0xFF) << 16 |
                (b[startpos + 3] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                //(byte) ((a >> 24) & 0xFF),
                //(byte) ((a >> 16) & 0xFF),
                //(byte) ((a >> 8) & 0xFF),
                //(byte) (a & 0xFF)
        };
    }

    public static long bytes2long(byte[] b, int start) throws Exception{
        long temp = 0;
        long res = 0;
        for (int i=0;i<8;i++) {
            res <<= 8;
            temp = b[start + i] & 0xff;
            res |= temp;
        }
        return res;
    }

    public String getSender() {
        return String.valueOf(etid);
    }

    public Long getTarget() {
        return etid;
    }

    public long getTime() {
        return time * 1000L;
    }

    public byte getType() {
        return type;
    }
}
