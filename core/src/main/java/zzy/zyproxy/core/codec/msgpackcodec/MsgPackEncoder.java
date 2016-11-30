package zzy.zyproxy.core.codec.msgpackcodec;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/11/23
 */
@ChannelHandler.Sharable
public class MsgPackEncoder extends OneToOneEncoder {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgPackEncoder.class);
    private Class clazz;

    public MsgPackEncoder(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg.getClass().equals(clazz)) {
            byte[] pack = MsgPackUtil.getMsgpacker().write(msg);
            if (pack != null) {
                LOGGER.debug("MsgPackEncoder，编码成功:{}", msg);
                return ctx.getChannel().getConfig().getBufferFactory().getBuffer(pack, 0, pack.length);
            }
            LOGGER.warn("MsgPackEncoder，编码错误:{}", msg);
            return ChannelBuffers.EMPTY_BUFFER;
        }
        return msg;
    }
}
