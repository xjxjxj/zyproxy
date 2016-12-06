package zzy.zyproxy.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.SharaChannels;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public abstract class BTPInboundHandler extends SimpleChannelInboundHandler<ProxyPacket> {
    private final static Logger LOGGER = LoggerFactory.getLogger(BTPInboundHandler.class);
    private final BTPChannel btpChannel;

    protected BTPInboundHandler(BTPChannel btpChannel) {
        super();
        this.btpChannel = btpChannel;
    }

    protected BTPChannel flushBTPChannel(ChannelHandlerContext ctx) {
        btpChannel.flushChannelHandlerContext(ctx);
        return btpChannel;
    }

    

    protected void channelRead0(ChannelHandlerContext ctx, ProxyPacket msg) throws Exception {
        BTPChannel btpChannel = flushBTPChannel(ctx);
        if (msg.isAuth()) {
            ProxyPacket.Auth auth = msg.asAuth();
            channelReadAuth(btpChannel, auth);
            return;
        }
        if (msg.isConnected()) {
            ProxyPacket.Connected connected = msg.asConnected();
            channelReadConnected(btpChannel, connected);
            return;
        }
        if (msg.isTransmit()) {
            ProxyPacket.Transmit transmit = msg.asTransmit();
            channelReadTransmit(btpChannel, transmit);
            return;
        }
        if (msg.isClose()) {
            ProxyPacket.Close close = msg.asClose();
            channelReadClose(btpChannel, close);
        }
    }

    protected abstract void channelReadAuth(BTPChannel btpChannel, ProxyPacket.Auth msg);

    protected abstract void channelReadConnected(BTPChannel btpChannel, ProxyPacket.Connected msg);

    protected abstract void channelReadTransmit(BTPChannel btpChannel, ProxyPacket.Transmit msg);

    protected abstract void channelReadClose(BTPChannel btpChannel, ProxyPacket.Close msg);
}
