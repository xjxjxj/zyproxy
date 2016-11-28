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

    private final ChannelShare channelShare;

    public AcceptNatBTPInboundHandler(ChannelShare channelShare) {
        this.channelShare = channelShare;
    }

    private UserNatBTPChannel userNatBTPChannel = new UserNatBTPChannel(null, null);

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
    }

    private void msgRealWriteToNatBTP(final UserNatBTPChannel.NatBTPChannel userNatBTPChannel, HeartMsg.RealWriteToNatBTP realWriteToNatBTP) {
        LOGGER.debug("msgRealWriteToNatBTP");
        userNatBTPChannel.writeToUser(realWriteToNatBTP.getMsgBody()).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                LOGGER.debug("msgRealWriteToNatBTP isSuccess:{},{},{}", future.isSuccess(),
                    userNatBTPChannel.getUserNatBTPChannel().getUserchannel().getChannel().isReadable(),
                    userNatBTPChannel.getUserNatBTPChannel().getUserchannel().getChannel().isWritable());
            }
        });
    }

    private void msgRealChannelConnected(UserNatBTPChannel.NatBTPChannel userNatBTPChannel, HeartMsg.RealChannelConnected realChannelConnected) {
        LOGGER.debug("msgRealChannelConnected");
        userNatBTPChannel.runStatusTask(UserNatBTPChannel.ChannelStatus.CONNECTED);
    }

    private void msgNatRegisterBTP(UserNatBTPChannel.NatBTPChannel userNatBTPChannel, HeartMsg msg0) {
        HeartMsg.NatRegisterBTPChannel natRegisterBTPChannel
            = msg0.asSubNatRegisterBTPChannel();
        LOGGER.debug("msgNatRegisterBTP,AcptUserPort:{}", natRegisterBTPChannel.getAcptUserPort());
        channelShare.putNatBTPChannel(userNatBTPChannel.getUserNatBTPChannel(), natRegisterBTPChannel.getAcptUserPort());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        super.exceptionCaught(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOGGER.debug("channelConnected");
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
    }
}
