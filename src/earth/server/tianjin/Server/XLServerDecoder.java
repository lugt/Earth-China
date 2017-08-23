package earth.server.tianjin.Server;

/**
 * Created by Frapo on 2017/1/24.
 */

import earth.Connection.XLRequest;
import earth.server.tianjin.Util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 客户端解码器
 */
public class XLServerDecoder extends ByteToMessageDecoder {

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
        int extend = buffer.readInt();
        int length = buffer.readInt(); // 数据包长

        if(length > 100000){
            buffer.resetReaderIndex();
        }

        if (buffer.readableBytes() < length) {
            buffer.resetReaderIndex();
            return;
        }

        XLRequest request = new XLRequest();

        request.setEncode(encode);

        request.setEncrypt(encrypt);

        request.setCommand(command);

        request.setExtend2(extend2);

        request.setSessionid(sessionid);

        request.setExtend(extend);

        request.setLength(length);
        //request.setIp(context.);

        if (length <= 0) {

        } else {
            ByteBuf dataBuffer = ByteBufAllocator.DEFAULT.directBuffer(length);
            buffer.readBytes(dataBuffer, length);
            request.setValues(ProtocolUtil.decode(encode, dataBuffer));
        }
        k.add(request);
    }
}
