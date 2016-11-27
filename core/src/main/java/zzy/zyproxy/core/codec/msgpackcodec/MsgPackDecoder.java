package zzy.zyproxy.core.codec.msgpackcodec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsg;

/**
 * @author zhouzhongyuan
 * @date 2016/11/23
 */
public class MsgPackDecoder extends OneToOneDecoder {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgPackDecoder.class);
    private Class clazz;

    public MsgPackDecoder(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof ChannelBuffer) {
            ChannelBuffer buffer = (ChannelBuffer) msg;
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            return MsgPackUtil.getMsgpacker().read(bytes, clazz);

        }
        LOGGER.debug("MsgPackDecoder@decode,msg class;{}", msg.getClass().getName());
        return null;
    }
}
