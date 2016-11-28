package zzy.zyproxy.netnat.natsrv.handler;

import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.natsrv.RealClientFactory;
import zzy.zyproxy.netnat.natsrv.channel.RealNatBTPChannel;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class NatBTPInboundHandler extends SimpleChannelUpstreamHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPInboundHandler.class);

    private final RealClientFactory realClientFactory;
    private final InetSocketAddress acptUserAddr;

    private RealNatBTPChannel realNatBTPChannel = new RealNatBTPChannel(null, null);

    public NatBTPInboundHandler(RealClientFactory realClientFactory, InetSocketAddress acptUserAddr) {
        this.realClientFactory = realClientFactory;
        this.acptUserAddr = acptUserAddr;
    }

    private RealNatBTPChannel.NatBTPChannel flushNatBTPChannel(Channel channel) {
        return realNatBTPChannel.flushNatBTPChannel(channel);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        flushNatBTPChannel(ctx.getChannel()).writeRegisterNatBTP(acptUserAddr);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (!(message instanceof HeartMsg)) {
            super.messageReceived(ctx, e);
            return;
        }
        Channel channel = ctx.getChannel();
        RealNatBTPChannel.NatBTPChannel natBTPChannel = flushNatBTPChannel(channel);
        HeartMsg msg0 = (HeartMsg) message;
        if (msg0.isUserChannelConnected()) {
            HeartMsg.UserChannelConnected userWriteToNatBTP
                = msg0.asSubUserChannelConnected();
            userChannelConnected(natBTPChannel, userWriteToNatBTP);
        }
        if (msg0.isUserWriteToNatBTP()) {
            HeartMsg.UserWriteToNatBTP userWriteToNatBTP = msg0.asSubUserWriteToNatBTP();
            msgUserWriteToNatBTP(natBTPChannel, userWriteToNatBTP);
        }
        if (msg0.isUserChannelClosed()) {
            HeartMsg.UserChannelClosed userChannelClosed = msg0.asSubUserChannelClosed();
            msgRealChannelClosed(natBTPChannel, userChannelClosed);
        }
    }

    private void msgRealChannelClosed(RealNatBTPChannel.NatBTPChannel natBTPChannel, HeartMsg.UserChannelClosed userChannelClosed) {
        LOGGER.debug("msgRealChannelClosed");
        natBTPChannel.userChannelClosed();
    }

    private void msgUserWriteToNatBTP(RealNatBTPChannel.NatBTPChannel natBTPChannel, HeartMsg.UserWriteToNatBTP userWriteToNatBTP) {
        LOGGER.debug("msgUserWriteToNatBTP");
        natBTPChannel.writeToReal(userWriteToNatBTP.getMsgBody());
    }

    private void userChannelConnected(final RealNatBTPChannel.NatBTPChannel natBTPChannel,
                                      HeartMsg.UserChannelConnected userWriteToNatBTP) {
        LOGGER.debug("userChannelConnected");
        RealNatBTPChannel realNatChannel = natBTPChannel.getRealNatChannel();
        final ChannelFuture realClient = realClientFactory.getRealClient(realNatChannel);
        realNatChannel.flushRealChannel(realClient.getChannel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LOGGER.warn("{}", e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
    }
}
