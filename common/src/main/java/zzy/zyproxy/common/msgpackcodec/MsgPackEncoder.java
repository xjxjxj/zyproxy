package zzy.zyproxy.common.msgpackcodec;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/11/23
 */
public class MsgPackEncoder extends OneToOneEncoder {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgPackEncoder.class);

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        return null;
    }
}
