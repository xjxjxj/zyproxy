package zzy.zyproxy.core.channel;

import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * @author zhouzhongyuan
 * @date 2016/11/23
 */
public abstract class ProxyChannelContext {
    private ChannelHandlerContext channelHandlerContext;

    public ProxyChannelContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
