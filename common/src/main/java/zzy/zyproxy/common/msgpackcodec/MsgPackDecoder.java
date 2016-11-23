package zzy.zyproxy.common.msgpackcodec;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/11/23
 */
public class MsgPackDecoder extends OneToOneDecoder {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgPackDecoder.class);

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        System.out.println(msg.getClass());
        return null;
    }
}
