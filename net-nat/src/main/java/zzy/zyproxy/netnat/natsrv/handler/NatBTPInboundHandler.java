package zzy.zyproxy.netnat.natsrv.handler;

import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.netnat.natsrv.RealClientFactory;
import zzy.zyproxy.netnat.natsrv.channel.NatBTPChannel;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class NatBTPInboundHandler extends SimpleChannelUpstreamHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPInboundHandler.class);

    private final RealClientFactory realClientFactory;
    private final InetSocketAddress acptUserAddr;

    private NatBTPChannel natBTPChannel = new NatBTPChannel(null);

    public NatBTPInboundHandler(RealClientFactory realClientFactory, InetSocketAddress acptUserAddr) {
        this.realClientFactory = realClientFactory;
        this.acptUserAddr = acptUserAddr;
    }

    private NatBTPChannel getNatBTPChannel(Channel channel) {
        return (NatBTPChannel) natBTPChannel.getHeartByChannel(channel);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        NatBTPChannel natBTPChannel = getNatBTPChannel(ctx.getChannel());
        LOGGER.debug("channelConnected");
        natBTPChannel.writeRegisterNatBTP(acptUserAddr);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        super.messageReceived(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        super.exceptionCaught(ctx, e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
    }
}
