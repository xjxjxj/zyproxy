package zzy.zyproxy.netlan.lansrv.handler;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import zzy.zyproxy.netlan.lansrv.RealClientFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class BackInboundHandler extends SimpleChannelUpstreamHandler {
    private final RealClientFactory realClientFactory;

    public BackInboundHandler(RealClientFactory realClientFactory) {

        this.realClientFactory = realClientFactory;
    }
}
