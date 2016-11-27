package zzy.zyproxy.netlan.lansrv.handler;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netlan.lansrv.BackClientFactory;
import zzy.zyproxy.netlan.lansrv.LanHeartClient;
import zzy.zyproxy.netlan.lansrv.channel.LanHeartChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouzhongyuan
 * @date 2016/11/26
 */
public class LanHeartInboundHandler extends SimpleChannelUpstreamHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(LanHeartInboundHandler.class);
    private int allIdleCount = 0;
    private LanHeartClient lanHeartClient;
    private final BackClientFactory backClientFactory;
    private final InetSocketAddress lanProxyAddr;
    private LanHeartChannel lanHeartChannel = new LanHeartChannel(null);

    public LanHeartInboundHandler(LanHeartClient lanHeartClient,
                                  BackClientFactory backClientFactory, InetSocketAddress lanProxyAddr) {
        this.lanHeartClient = lanHeartClient;
        this.backClientFactory = backClientFactory;
        this.lanProxyAddr = lanProxyAddr;
    }

    private LanHeartChannel getLanHeartChannel(Channel channel) {
        return (LanHeartChannel) lanHeartChannel.getHeartByChannel(channel);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        getLanHeartChannel(ctx.getChannel()).writeRegisterLanHeart(lanProxyAddr);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message0 = e.getMessage();
        Object message = e.getMessage();
        if (!(message instanceof HeartMsg)) {
            super.messageReceived(ctx, e);
            return;
        }
        HeartMsg msg0 = (HeartMsg) message0;
        if (msg0.isPong()) {
            msgPong(ctx, msg0);
        }
        if (msg0.isNetRequestNewChannel()) {
            msgNetRequestNewChannel(ctx, msg0);
        }
    }

    private void msgNetRequestNewChannel(ChannelHandlerContext ctx, HeartMsg msg0) {
        HeartMsg.NetRequestNewChannel netRequestNewChannel = msg0.asSubNetRequestNewChannel();
        backClientFactory.getBackClient();

    }

    private void msgPong(ChannelHandlerContext ctx, HeartMsg msg0) {
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
                lanHeartClient.reConnect();
                return;
            }
            LOGGER.debug("LAN端，心跳检测，长时间没有{}@{}", state, ctx.getChannel());
            getLanHeartChannel(ctx.getChannel()).writePing();
        }
    }
}
