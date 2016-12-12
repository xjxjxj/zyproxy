package zzy.zyproxy.netnat.nat.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.ChannelUtil;
import zzy.zyproxy.netnat.channel.NetNatNaturalChannel;
import zzy.zyproxy.netnat.nat.channel.NatBTPChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public class NatBTPHandler extends SimpleChannelInboundHandler<ProxyPacket> {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPHandler.class);
    private final BTPChannel natBTPChannel;

    public NatBTPHandler(BTPChannel natBTPChannel) {
        super();
        if (natBTPChannel == null) {
            throw new NullPointerException("NatBTPHandler#natBTPChannel");
        }
        this.natBTPChannel = natBTPChannel;
    }

    protected BTPChannel flushBTPChannel(ChannelHandlerContext ctx) {
        natBTPChannel.flushChannelHandlerContext(ctx);
        return natBTPChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        flushBTPChannel(ctx).channelActive();
    }


    protected void channelRead0(ChannelHandlerContext ctx, ProxyPacket msg) throws Exception {
        BTPChannel btpChannel = flushBTPChannel(ctx);
        if (msg.isAuth()) {
            ProxyPacket.Auth auth = msg.asAuth();
            btpChannel.channelReadAuth(auth);
            return;
        }
        if (msg.isConnected()) {
            ProxyPacket.Connected connected = msg.asConnected();
            btpChannel.channelReadConnected(connected);
            return;
        }
        if (msg.isTransmit()) {
            ProxyPacket.Transmit transmit = msg.asTransmit();
            LOGGER.debug("ProxyPacket.transmit:{}", transmit.getUserCode());
            btpChannel.channelReadTransmit(transmit);
            return;
        }
        if (msg.isClose()) {
            ProxyPacket.Close close = msg.asClose();
            LOGGER.debug("ProxyPacket.close:{}", close.getUserCode());
            btpChannel.channelReadClose(close);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("{}", this.getClass().getSimpleName(), cause);
    }
}
