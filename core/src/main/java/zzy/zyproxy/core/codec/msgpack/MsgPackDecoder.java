package zzy.zyproxy.core.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;

import java.util.List;

/**
 * @author zhouzhongyuan
 * @date 2016/12/2
 */
public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgPackDecoder.class);

    Class<? extends ProxyPacket> clazz;

    public MsgPackDecoder setClazz(Class<? extends ProxyPacket> clazz) {
        this.clazz = clazz;
        return this;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        ProxyPacket read = MsgPackUtil.getMsgpacker().read(byteBuf.nioBuffer(), clazz);
        list.add(read);
    }
}
