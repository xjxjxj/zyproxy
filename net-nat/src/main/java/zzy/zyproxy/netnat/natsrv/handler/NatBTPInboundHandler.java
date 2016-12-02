package zzy.zyproxy.netnat.natsrv.handler;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateEvent;
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

    private RealNatBTPChannel realNatBTPChannel = new RealNatBTPChannel(null);
    private int allIdleCount = 0;

    public NatBTPInboundHandler(RealClientFactory realClientFactory, InetSocketAddress acptUserAddr) {
        this.realClientFactory = realClientFactory;
        this.acptUserAddr = acptUserAddr;
    }

    private RealNatBTPChannel flushChannel(Channel channel) {
        return realNatBTPChannel.flushChannel(channel);
    }

    //==超时检测
    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        RealNatBTPChannel realNatBTPChannel
            = flushChannel(ctx.getChannel());
        if (e instanceof IdleStateEvent) {
            channelIdle(realNatBTPChannel, (IdleStateEvent) e);
        }
        super.handleUpstream(ctx, e);
    }

    private void channelIdle(RealNatBTPChannel realNatBTPChannel, IdleStateEvent e) {
        IdleState state = e.getState();
        if (state.equals(IdleState.ALL_IDLE)) {
            allIdleCount++;
            if (allIdleCount > 3) {
                realNatBTPChannel.close();
                return;
            }
            LOGGER.debug("LAN端，心跳检测，长时间没有{}@{}", state, realNatBTPChannel);
            realNatBTPChannel.writePing();
        }
    }

    //==生命周期
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        flushChannel(ctx.getChannel()).writeRegisterNatBTP(acptUserAddr);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (!(message instanceof HeartMsg)) {
            super.messageReceived(ctx, e);
            return;
        }
        Channel channel = ctx.getChannel();
        RealNatBTPChannel realNatBTPChannel = flushChannel(channel);
        HeartMsg msg0 = (HeartMsg) message;
        if (msg0.isPong()) {
            HeartMsg.Pong pong = msg0.asSubPong();
            msgPong(realNatBTPChannel, pong);
        }
        if (msg0.isConnected()) {
            HeartMsg.Connected userWriteToNatBTP
                = msg0.asSubConnected();
            msgUserConnected(realNatBTPChannel, userWriteToNatBTP);
        }
        if (msg0.isTransferBody()) {
            HeartMsg.TransferBody transferBody = msg0.asSubTransferBody();
            msgTransferBody(realNatBTPChannel, transferBody);
        }
        if (msg0.isClosed()) {
            HeartMsg.Closed userChannelClosed = msg0.asSubClosed();
            msgUserChannelClosed(realNatBTPChannel, userChannelClosed);
        }
    }

    private void msgPong(RealNatBTPChannel natBTPChannel, HeartMsg.Pong msg0) {
        allIdleCount = 0;
    }

    private void msgUserChannelClosed(RealNatBTPChannel realNatBTPChannel, HeartMsg.Closed userChannelClosed) {
        LOGGER.debug("msgUserChannelClosed");
        realNatBTPChannel.userChannelClosed(userChannelClosed.getUserCode());
    }

    private void msgTransferBody(RealNatBTPChannel realNatBTPChannel, HeartMsg.TransferBody transferBody) {
        LOGGER.debug("msgTransferBody");
        realNatBTPChannel.writeToReal(transferBody.getMsgBody(),transferBody.getUserCode());
    }

    private void msgUserConnected(final RealNatBTPChannel realNatBTPChannel,
                                  HeartMsg.Connected connected) {
        LOGGER.debug("msgUserConnected");
        //new realChannel 稍后进行channel刷新
        RealNatBTPChannel.RealChannel realChannel = realNatBTPChannel.newRealChannel(connected.getUserCode());
        realClientFactory.createRealClient(realChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LOGGER.warn("{}", e);
    }
}
