package zzy.zyproxy.netnat.netsrv.handler;

import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.netsrv.ChannelShare;
import zzy.zyproxy.netnat.netsrv.channel.NatChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class AcceptNatInboundHandler extends SimpleChannelUpstreamHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptNatInboundHandler.class);

    private final ChannelShare channelShare;

    public AcceptNatInboundHandler(ChannelShare channelShare) {
        this.channelShare = channelShare;
    }

    private NatChannel natChannel = new NatChannel(null);

    private NatChannel getNatChannel(Channel channel) {
        return (NatChannel) natChannel.getHeartByChannel(channel);
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
        NatChannel natChannel = getNatChannel(channel);
        if (msg0.isNatRegisterBTPChannel()) {
            msgLanRegisterBack(natChannel, msg0);
        }
    }

    private void msgLanRegisterBack(NatChannel natChannel, HeartMsg msg0) {
        HeartMsg.NatRegisterBTPChannel natRegisterBTPChannel = msg0.asSubNatRegisterBTPChannel();
        LOGGER.debug("msgLanRegisterBack,AcptUserPort:{}", natRegisterBTPChannel.getAcptUserPort());
        channelShare.putNatChannel(natChannel, natRegisterBTPChannel.getAcptUserPort());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        super.exceptionCaught(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
    }
}
