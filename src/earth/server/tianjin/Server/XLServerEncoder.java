package earth.server.tianjin.Server;

/**
 * Created by Frapo on 2017/1/24.
 *
 * @ex-author hankchen
 * 2012-2-3 上午10:48:15
 */

import earth.Connection.XLResponse;
import earth.server.tianjin.Util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

/**
 * 服务器端编码器
 */

public class XLServerEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out){
        //out.writeBytes(ByteObjConverter.objectToByte(msg));
        if (msg instanceof XLResponse) {
            XLResponse response = (XLResponse) msg;
            ByteBuffer headBuffer = ByteBuffer.allocate(16);
            /**
             * 先组织报文头
             */
            headBuffer.put(response.getEncode());
            headBuffer.put(response.getEncrypt());
            headBuffer.put(response.getCommand());
            headBuffer.put(response.getExtend2()); // (4) + SSID + RES + LEN

            headBuffer.putInt(response.getSessionid());
            headBuffer.putInt(response.getResult());

            /**
             * 组织报文的数据部分
             */
            ByteBuf dataBuffer = ProtocolUtil.encode(response.getEncode(), response.getValues());

            int length = 0;
            if(dataBuffer != null) {
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
            //Monitor.logger("sent["+out.readableBytes()+"]");
            //logger.info("totalBuffer size=" + totalBuffer.readableBytes());
            //buf.write(ctx, .getFuture(), totalBuffer);
        }
    }
}

