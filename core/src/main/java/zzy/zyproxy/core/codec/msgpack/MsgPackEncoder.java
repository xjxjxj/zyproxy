package zzy.zyproxy.core.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/12/2
 */
public class MsgPackEncoder extends MessageToByteEncoder<MsgpackPacket> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgPackEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, MsgpackPacket msg, ByteBuf out) throws Exception {
        try {
            byte[] write = MsgPackUtil.getMsgpacker().write(msg);
            out.writeBytes(write);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
