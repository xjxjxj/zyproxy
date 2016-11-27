package zzy.zyproxy.netnat.natsrv.handler;

import org.jboss.netty.channel.*;
import zzy.zyproxy.netnat.natsrv.RealClientFactory;
import zzy.zyproxy.netnat.natsrv.channel.NatBackChannel;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class NatInboundHandler extends SimpleChannelUpstreamHandler {
    private final RealClientFactory realClientFactory;
    private final InetSocketAddress acptUserAddr;

    private NatBackChannel natBackChannel = new NatBackChannel(null);
    public NatInboundHandler(RealClientFactory realClientFactory, InetSocketAddress acptUserAddr) {
        this.realClientFactory = realClientFactory;
        this.acptUserAddr = acptUserAddr;
    }
    private NatBackChannel getLanBackChannel(Channel channel) {
        return (NatBackChannel) natBackChannel.getHeartByChannel(channel);
    }
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        NatBackChannel natBackChannel = getLanBackChannel(ctx.getChannel());
        natBackChannel.writeRegisterLanBack(acptUserAddr);
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
