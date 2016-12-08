package zzy.zyproxy.netnat.nat.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.ChannelUtil;
import zzy.zyproxy.netnat.channel.NetNatNaturalChannel;
import zzy.zyproxy.netnat.nat.RealClientFactory;
import zzy.zyproxy.netnat.nat.channel.NatBTPChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public class NatBTPHandler extends SimpleChannelInboundHandler<ProxyPacket> {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPHandler.class);
    private final String auth;
    private final RealClientFactory realClientFactory;
    private final NatBTPChannel btpChannel;

    public NatBTPHandler(NatBTPChannel btpChannel, String auth, RealClientFactory realClientFactory) {
        super();
        this.btpChannel = btpChannel;
        this.auth = auth;
        this.realClientFactory = realClientFactory;
    }

    protected NatBTPChannel flushBTPChannel(ChannelHandlerContext ctx) {
        if (btpChannel == null) {
            ChannelUtil.flushAndClose(ctx);
            return null;
        }
        btpChannel.flushChannelHandlerContext(ctx);
        return btpChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        BTPChannel btpChannel = flushBTPChannel(ctx);
        active(btpChannel);
    }


    protected void channelRead0(ChannelHandlerContext ctx, ProxyPacket msg) throws Exception {
        try {
            NatBTPChannel btpChannel = flushBTPChannel(ctx);
            if (msg.isAuth()) {
                ProxyPacket.Auth auth = msg.asAuth();
                LOGGER.debug("ProxyPacket.Auth:{}", auth.getAuthCode());
                channelReadAuth(btpChannel, auth);
                return;
            }
            if (msg.isConnected()) {
                ProxyPacket.Connected connected = msg.asConnected();
                LOGGER.debug("ProxyPacket.Connected:{}", connected.getUserCode());
                channelReadConnected(btpChannel, connected);
                return;
            }
            if (msg.isTransmit()) {
                ProxyPacket.Transmit transmit = msg.asTransmit();
                LOGGER.debug("ProxyPacket.transmit:{}", transmit.getUserCode());
                channelReadTransmit(btpChannel, transmit);
                return;
            }
            if (msg.isClose()) {
                ProxyPacket.Close close = msg.asClose();
                LOGGER.debug("ProxyPacket.close:{}", close.getUserCode());
                channelReadClose(btpChannel, close);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("{}", this.getClass().getSimpleName(), cause);
    }


    protected void active(BTPChannel btpChannel) {
        btpChannel.writeAuth(auth);
    }

    protected void channelReadAuth(NatBTPChannel btpChannel, ProxyPacket.Auth msg) {
        if (!auth.equals(msg.getAuthCode())) {
            btpChannel.flushAndClose();
        } else {
            realClientFactory.setNatBTPChannel(btpChannel);
        }
    }

    protected void channelReadConnected(NatBTPChannel btpChannel, ProxyPacket.Connected msg) {
        NetNatNaturalChannel netNatNaturalChannel = new NetNatNaturalChannel(msg.getUserCode(), btpChannel);
        btpChannel.getUsersQueue().add(netNatNaturalChannel);
        realClientFactory.createClient();
    }

    protected void channelReadTransmit(NatBTPChannel btpChannel, ProxyPacket.Transmit msg) {
        NaturalChannel naturalChannel = btpChannel.getNaturalChannel(msg.getUserCode());
        naturalChannel.writeMsgAndFlush(msg.getBody());
    }

    protected void channelReadClose(NatBTPChannel btpChannel, ProxyPacket.Close msg) {
        NaturalChannel naturalChannel = btpChannel.getNaturalChannel(msg.getUserCode());
        naturalChannel.flushAndClose();
    }
}
