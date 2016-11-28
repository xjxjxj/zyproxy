package zzy.zyproxy.netnat.netsrv.handler;

import org.jboss.netty.channel.*;
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
        userNatBTPChannel = new UserNatBTPChannel(null, null, channelShare);
    }

    private UserNatBTPChannel.NatBTPChannel flushNatBTPChannel(Channel channel) {
        return userNatBTPChannel.flushNatBTPChannel(channel);
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
        UserNatBTPChannel.NatBTPChannel natBTPChannel = flushNatBTPChannel(channel);
        if (msg0.isNatRegisterBTPChannel()) {
            msgNatRegisterBTP(natBTPChannel, msg0);
        }
        if (msg0.isRealChannelConnected()) {
            HeartMsg.RealChannelConnected realChannelConnected
                = msg0.asSubRealChannelConnected();
            msgRealChannelConnected(natBTPChannel, realChannelConnected);
        }
        if (msg0.isRealWriteToNatBTP()) {
            HeartMsg.RealWriteToNatBTP realWriteToNatBTP
                = msg0.asSubRealWriteToNatBTP();
            msgRealWriteToNatBTP(natBTPChannel, realWriteToNatBTP);
        }
        if (msg0.isRealChannelClosed()) {
            HeartMsg.RealChannelClosed realChannelClosed
                = msg0.asSubRealChannelClosed();
            msgRealChannelClosed(natBTPChannel, realChannelClosed);
        }
    }

    private void msgRealChannelClosed(UserNatBTPChannel.NatBTPChannel natBTPChannel, HeartMsg.RealChannelClosed realChannelClosed) {
        LOGGER.debug("msgRealChannelClosed");
        natBTPChannel.realChannelClosed();
    }

    private void msgRealWriteToNatBTP(final UserNatBTPChannel.NatBTPChannel userNatBTPChannel, HeartMsg.RealWriteToNatBTP realWriteToNatBTP) {
        LOGGER.debug("msgRealWriteToNatBTP");
        userNatBTPChannel.writeToUser(realWriteToNatBTP.getMsgBody());
    }

    private void msgRealChannelConnected(UserNatBTPChannel.NatBTPChannel userNatBTPChannel, HeartMsg.RealChannelConnected realChannelConnected) {
        LOGGER.debug("msgRealChannelConnected");
        userNatBTPChannel.realChannelConnected();
    }

    private void msgNatRegisterBTP(UserNatBTPChannel.NatBTPChannel natBTPChannel, HeartMsg msg0) {
        HeartMsg.NatRegisterBTPChannel natRegisterBTPChannel
            = msg0.asSubNatRegisterBTPChannel();
        int acptUserPort = natRegisterBTPChannel.getAcptUserPort();
        LOGGER.debug("msgNatRegisterBTP,AcptUserPort:{}", acptUserPort);
        natBTPChannel.setAcptUserPort(acptUserPort);
        channelShare.putNatBTPChannel(natBTPChannel.getUserNatBTPChannel(), acptUserPort);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LOGGER.warn("{}", e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOGGER.debug("writeChannelConnected");
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
    }
}
