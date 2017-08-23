package earth.client.Long;

/**
 * Created by Frapo on 2017/1/24.
 */

import earth.Connection.XLResponse;
import earth.client.Util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.HashMap;
import java.util.List;

/**
 * 客户端解码器
 */
public class XLClientDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> k) throws Exception {

        if (buffer.readableBytes() < 16) {
            return;
        }
        buffer.markReaderIndex();
        byte encode = buffer.readByte();
        byte encrypt = buffer.readByte();
        byte command = buffer.readByte();
        byte extend2 = buffer.readByte();

        int sessionid = buffer.readInt();
        int result = buffer.readInt();

        int length = buffer.readInt(); // 数据包长
        if (buffer.readableBytes() < length) {
            buffer.resetReaderIndex();
            return;
        }

        XLResponse response = new XLResponse();

        response.setEncode(encode);

        response.setEncrypt(encrypt);

        response.setCommand(command);

        response.setExtend2(extend2);

        response.setSessionid(sessionid);

        response.setResult(result);

        response.setLength(length);

        //response.setIp(context.channel().remoteAddress().toString());
        //response.setIp(context.);

        if (length <= 0) {
            response.setValues(new HashMap<String, String>());
        } else {

            ByteBuf dataBuffer = ByteBufAllocator.DEFAULT.directBuffer(length);

            buffer.readBytes(dataBuffer, length);

            response.setValues(ProtocolUtil.decode(encode, dataBuffer));
        }
        k.add(response);
    }

}
