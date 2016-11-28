package zzy.zyproxy.netnat.natsrv.handler;

import org.jboss.netty.channel.*;
import zzy.zyproxy.netnat.natsrv.channel.RealNatBTPChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class RealInboundHandler extends SimpleChannelUpstreamHandler {
    private RealNatBTPChannel realNatBTPChannel;

    public RealInboundHandler(RealNatBTPChannel realNatBTPChannel) {
        this.realNatBTPChannel = realNatBTPChannel;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        final Channel channel = ctx.getChannel();
        realNatBTPChannel.flushRealChannel(channel);
        channel.setReadable(false);
        realNatBTPChannel.writeRealChannelConnected().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    channel.setReadable(true);
                }
            }
        });
    }
}
