package zzy.zyproxy.netnat.net.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.ChannelUtil;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public class AcceptBTPHandler extends SimpleChannelInboundHandler<ProxyPacket> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptBTPHandler.class);
    private final BTPChannel btpChannel;

    public AcceptBTPHandler(BTPChannel btpChannel) {
        super();
        if (btpChannel == null){
            throw new NullPointerException("AcceptBTPHandler#BTPChannel");
        }
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
            btpChannel.channelReadTransmit(transmit);
            return;
        }
        if (msg.isClose()) {
            ProxyPacket.Close close = msg.asClose();
            btpChannel.channelReadClose(close);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("{}", cause);
    }
}
