package earth.Connection;

/**
 * Created by Frapo on 2017/1/22.
 */
public class MsgType {


    // 客户端请求初始化
    public static final byte CONNCET = 0x05;

    // 心跳
    public static final byte PING = 0x0a;
    public static final byte PONG = 0x0b;

    // 服务器反馈
    public static final byte RECONNCET = 0x0f; // 服务器分亏
    public static final byte CONNSUCCESS = 0x30; // 服务器分亏

    // 客户端请求
    public static final byte SEND = 0x40;
    public static final byte FETCHMSG = 0x41;

    // 服务器ACK
    public static final byte SENDACK = 0x50;

    //服务器推送
    public static final byte MSGARRIVE = 0x7a;
    public static final byte MSGARRIVEACK = 0x7b;

    public static final byte MSGSYNC = 0x7d;
    public static final byte FETCHMSGRTN = 0x7e;

// 收到不回复

    // 错误信息
    public static final byte EXCEPT = 0x06;
}