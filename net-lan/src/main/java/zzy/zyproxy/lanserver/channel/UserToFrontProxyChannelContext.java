package zzy.zyproxy.lanserver.channel;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.ProxyChannelContext;

/**
 * @author zhouzhongyuan
 * @date 2016/11/25
 */
public class UserToFrontProxyChannelContext extends ProxyChannelContext {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserToFrontProxyChannelContext.class);

    public UserToFrontProxyChannelContext(ChannelHandlerContext channelHandlerContext) {
        super(channelHandlerContext);
    }
}
