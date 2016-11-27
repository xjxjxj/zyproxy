package zzy.zyproxy.netnat.natsrv.handler;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.natsrv.NatClientFactory;
import zzy.zyproxy.netnat.natsrv.NatHeartClient;
import zzy.zyproxy.netnat.natsrv.channel.NatHeartChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouzhongyuan
 * @date 2016/11/26
 */
public class NatHeartInboundHandler extends SimpleChannelUpstreamHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatHeartInboundHandler.class);
    private int allIdleCount = 0;
    private NatHeartClient natHeartClient;
    private final NatClientFactory natClientFactory;
    private final InetSocketAddress acptUserAddr;
    private NatHeartChannel natHeartChannel = new NatHeartChannel(null);

    public NatHeartInboundHandler(NatHeartClient natHeartClient,
                                  NatClientFactory natClientFactory, InetSocketAddress acptUserAddr) {
        this.natHeartClient = natHeartClient;
        this.natClientFactory = natClientFactory;
        this.acptUserAddr = acptUserAddr;
    }

    private NatHeartChannel getLanHeartChannel(Channel channel) {
        return (NatHeartChannel) natHeartChannel.getHeartByChannel(channel);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        getLanHeartChannel(ctx.getChannel()).writeRegisterLanHeart(acptUserAddr);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message0 = e.getMessage();
        Object message = e.getMessage();
        if (!(message instanceof HeartMsg)) {
            super.messageReceived(ctx, e);
            return;
        }
        NatHeartChannel natHeartChannel = getLanHeartChannel(ctx.getChannel());
        HeartMsg msg0 = (HeartMsg) message0;
        if (msg0.isPong()) {
            msgPong(natHeartChannel, msg0);
        }
        if (msg0.isNetRequestBTPChannel()) {
            msgNetRequestNewChannel(natHeartChannel, msg0);
        }
    }

    private void msgNetRequestNewChannel(final NatHeartChannel natHeartChannel, HeartMsg msg0) {
        HeartMsg.NetRequestBTPChannel netRequestBTPChannel = msg0.asSubNetRequestBTPChannel();
        natClientFactory.getBackClient(netRequestBTPChannel.getNetRequestNewChannelNum()).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    LOGGER.debug("msgNetRequestNewChannel#future.isSuccess");
                    natHeartChannel.writeLanResponseNewChannel();
                }
            }
        });
    }

    private void msgPong(NatHeartChannel ctx, HeartMsg msg0) {
        allIdleCount = 0;
    }


    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        getLanHeartChannel(ctx.getChannel());
        if (e instanceof IdleStateEvent) {
            channelIdle(ctx, (IdleStateEvent) e);
        }
        super.handleUpstream(ctx, e);
    }

    private void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
        IdleState state = e.getState();
        if (state.equals(IdleState.ALL_IDLE)) {
            allIdleCount++;
            if (allIdleCount > 3) {
                ctx.getChannel().close().awaitUninterruptibly(10, TimeUnit.SECONDS);
                natHeartClient.reConnect();
                return;
            }
            LOGGER.debug("LAN端，心跳检测，长时间没有{}@{}", state, ctx.getChannel());
            getLanHeartChannel(ctx.getChannel()).writePing();
        }
    }
}