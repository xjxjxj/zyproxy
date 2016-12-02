package zzy.zyproxy.netnat.netsrv.handler;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.netsrv.ChannelShare;
import zzy.zyproxy.netnat.netsrv.channel.UserNatBTPChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class AcceptNatBTPInboundHandler extends SimpleChannelUpstreamHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptNatBTPInboundHandler.class);

    private ChannelShare channelShare;
    private UserNatBTPChannel userNatBTPChannel;

    public AcceptNatBTPInboundHandler(ChannelShare channelShare) {
        this.channelShare = channelShare;
        userNatBTPChannel = new UserNatBTPChannel(null);
        System.out.println(userNatBTPChannel);
    }

    private UserNatBTPChannel flushNatBTPChannel(Channel channel) {
        return userNatBTPChannel.flushChannel(channel);
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
        UserNatBTPChannel userNatBTPChannel = flushNatBTPChannel(channel);
        if (msg0.isNatRegisterBTPChannel()) {
            HeartMsg.NatRegisterBTPChannel natRegisterBTPChannel = msg0.asSubNatRegisterBTPChannel();
            msgNatRegisterBTP(userNatBTPChannel, natRegisterBTPChannel);
        }
        if (msg0.isPing()) {
            HeartMsg.Ping ping
                = msg0.asSubPing();
            msgPing(userNatBTPChannel, ping);
        }
        if (msg0.isConnected()) {
            HeartMsg.Connected realConnected
                = msg0.asSubConnected();
            msgRealConnected(userNatBTPChannel, realConnected);
        }
        if (msg0.isTransferBody()) {
            HeartMsg.TransferBody transferBody
                = msg0.asSubTransferBody();
            msgTransferBody(userNatBTPChannel, transferBody);
        }
        if (msg0.isClosed()) {
            HeartMsg.Closed closed
                = msg0.asSubClosed();
            msgRealChannelClosed(userNatBTPChannel, closed);
        }
    }

    private void msgPing(final UserNatBTPChannel natBTPChannel, final HeartMsg.Ping msg) {
        natBTPChannel.writePong();
    }

    private void msgRealChannelClosed(UserNatBTPChannel userNatBTPChannel, HeartMsg.Closed closed) {
        LOGGER.debug("msgRealChannelClosed");
        userNatBTPChannel.realChannelClosed(closed.getUserCode());
    }

    private void msgTransferBody(final UserNatBTPChannel userNatBTPChannel, HeartMsg.TransferBody transferBody) {
        LOGGER.debug("msgRealWriteToNatBTP");
        userNatBTPChannel.writeToUser(transferBody.getMsgBody(),transferBody.getUserCode());
    }

    private void msgRealConnected(UserNatBTPChannel userNatBTPChannel, HeartMsg.Connected realConnected) {
        LOGGER.debug("msgRealConnected");
        userNatBTPChannel.realConnected(realConnected.getUserCode());
    }

    private void msgNatRegisterBTP(UserNatBTPChannel natBTPChannel, HeartMsg.NatRegisterBTPChannel msg) {
        int acptUserPort = msg.getAcptUserPort();
        LOGGER.debug("msgNatRegisterBTP,AcptUserPort:{}【开始注册】", acptUserPort);
        channelShare.addUserNatBTPChannel(natBTPChannel, acptUserPort);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LOGGER.warn("{}", e);
    }

    /**
     * ----------
     * IdleStateEvent 超时检测
     */
    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof IdleStateEvent) {
            UserNatBTPChannel userNatBTPChannel = flushNatBTPChannel(ctx.getChannel());
            channelIdle(userNatBTPChannel, (IdleStateEvent) e);
        }
        super.handleUpstream(ctx, e);
    }

    private void channelIdle(UserNatBTPChannel userNatBTPChannel, IdleStateEvent e) {
        IdleState state = e.getState();
        if (state.equals(IdleState.ALL_IDLE)) {
            LOGGER.debug("NET端，心跳检测，长时间没有{}@{} 关闭NatBTPChannel", state, userNatBTPChannel);
            userNatBTPChannel.close();
        }
    }
}
