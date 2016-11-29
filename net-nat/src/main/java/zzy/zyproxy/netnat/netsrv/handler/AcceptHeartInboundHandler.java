package zzy.zyproxy.netnat.netsrv.handler;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.netsrv.ChannelShare;
import zzy.zyproxy.netnat.netsrv.channel.NetHeartChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public class AcceptHeartInboundHandler extends SimpleChannelUpstreamHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptHeartInboundHandler.class);

    private final ChannelShare channelShare;
    private NetHeartChannel netHeartChannel = new NetHeartChannel(null);

    public AcceptHeartInboundHandler(ChannelShare channelShare) {
        this.channelShare = channelShare;
    }

    private NetHeartChannel flushNetHeartChannel(Channel channel) {
        return netHeartChannel.flushChannel(channel);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (!(message instanceof HeartMsg)) {
            super.messageReceived(ctx, e);
            return;
        }
        //------
        HeartMsg msg0 = (HeartMsg) message;
        Channel channel = ctx.getChannel();
        NetHeartChannel netHeartChannel = flushNetHeartChannel(channel);
        if (msg0.isPing()) {
            msgPing(netHeartChannel, msg0);
        }
        if (msg0.isNatRegisterHeart()) {
            msgRegisterLanHeart(netHeartChannel, msg0);
        }
        if (msg0.isNatResponseBTPChannel()) {
            msgLanResponseNewChannel(netHeartChannel, msg0);
        }
    }

    private void msgLanResponseNewChannel(NetHeartChannel netHeartChannel, HeartMsg msg0) {
        HeartMsg.NatResponseBTPChannel natResponseBTPChannel = msg0.asSubNatResponseBTPChannel();

    }

    private void msgRegisterLanHeart(NetHeartChannel netHeartChannel, HeartMsg msg0) {
        LOGGER.info("msgRegisterLanHeart@{}", netHeartChannel);

        HeartMsg.NatRegisterHeart natRegisterHeart = msg0.asSubNatRegisterHeart();
        channelShare.putNewHeartChannel(netHeartChannel, natRegisterHeart.getNetAcptUserPort());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LOGGER.info("exceptionCaught@{},{}", ctx.getChannel(), e);
        ctx.getChannel().close();
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOGGER.info("channelClosed@{}", ctx.getChannel());
        channelShare.newCloseBackSrvTask(ctx.getChannel());
    }


    /**
     * 心跳的处理
     *
     * @param netHeartChannel 连接
     * @param heartMsg0       原始信息
     */
    private void msgPing(final NetHeartChannel netHeartChannel, final HeartMsg heartMsg0) {
        netHeartChannel.writePong();
    }

    /**
     * ----------
     * IdleStateEvent 超时检测
     *
     * @param ctx
     * @param e
     * @throws Exception
     */
    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof IdleStateEvent) {
            channelIdle(ctx, (IdleStateEvent) e);
        }
        super.handleUpstream(ctx, e);
    }

    private void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
        IdleState state = e.getState();
        if (state.equals(IdleState.ALL_IDLE)) {
            LOGGER.debug("NET端，心跳检测，长时间没有{}@{}", state, ctx.getChannel());
            ctx.getChannel().close();
        }
    }
    //------------


}
