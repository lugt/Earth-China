package earth.client.Long;

import java.nio.ByteBuffer;

import earth.Connection.XLRequest;
import earth.client.Util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Frapo on 2017/1/27.
 */

public class XLClientEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        //out.writeBytes(ByteObjConverter.objectToByte(msg));
        if (msg instanceof XLRequest) {
            XLRequest request = (XLRequest) msg;
            ByteBuffer headBuffer = ByteBuffer.allocate(16);
            /**
             * 先组织报文头
             */
            headBuffer.put(request.getEncode());
            headBuffer.put(request.getEncrypt());
            headBuffer.put(request.getCommand());
            headBuffer.put(request.getExtend2());

            headBuffer.putInt(request.getSessionid());
            headBuffer.putInt(request.getExtend());
            /**
             * 组织报文的数据部分
             */
            ByteBuf dataBuffer = ProtocolUtil.encode(request.getEncode(), request.getValues());


            int length = 0;

            if(dataBuffer != null){
                length = dataBuffer.readableBytes();
            }

            headBuffer.putInt(length);
            /**
             * 非常重要
             * ByteBuffer需要手动flip()，ChannelBuffer不需要
             */
            headBuffer.flip();
            //ChannelBuffer totalBuffer = ChannelBuffers.dynamicBuffer();
            // 再一次的复制（第二次复制，前一次是在ProtocolUtil中进行的）
            out.writeBytes(headBuffer);
            if(dataBuffer != null) {
                out.writeBytes(dataBuffer);
            }

            //Monitor.logger("Sent bytes : " + out.readableBytes());
            //log.info("totalBuffer size=" + totalBuffer.readableBytes());
            //buf.write(ctx, .getFuture(), totalBuffer);
        }
    }
}

