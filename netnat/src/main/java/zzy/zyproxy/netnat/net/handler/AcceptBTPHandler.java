package zzy.zyproxy.netnat.net.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.ChannelUtil;
import zzy.zyproxy.core.util.SharaChannels;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public class AcceptBTPHandler extends SimpleChannelInboundHandler<ProxyPacket> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptBTPHandler.class);
    private final BTPChannel btpChannel;
    private final SharaChannels sharaChannels;

    public AcceptBTPHandler(BTPChannel btpChannel, SharaChannels sharaChannels) {
        super();
        this.btpChannel = btpChannel;
        this.sharaChannels = sharaChannels;
    }

    protected BTPChannel flushBTPChannel(ChannelHandlerContext ctx) {
        if (btpChannel == null) {
            ChannelUtil.flushAndClose(ctx);
            return null;
        }
        btpChannel.flushChannelHandlerContext(ctx);
        return btpChannel;
    }

    protected void channelRead0(ChannelHandlerContext ctx, ProxyPacket msg) throws Exception {
        try {
            BTPChannel btpChannel = flushBTPChannel(ctx);
            if (msg.isAuth()) {
                ProxyPacket.Auth auth = msg.asAuth();
                LOGGER.debug("ProxyPacket.Auth:AuthCode{}", auth.getAuthCode());
                channelReadAuth(btpChannel, auth);
                return;
            }
            if (msg.isConnected()) {
                ProxyPacket.Connected connected = msg.asConnected();
                LOGGER.debug("ProxyPacket.Connected:UserCode:{}", connected.getUserCode());
                channelReadConnected(btpChannel, connected);
                return;
            }
            if (msg.isTransmit()) {
                ProxyPacket.Transmit transmit = msg.asTransmit();
                LOGGER.debug("ProxyPacket.transmit:UserCode:{}", transmit.getUserCode());
                channelReadTransmit(btpChannel, transmit);
                return;
            }
            if (msg.isClose()) {
                ProxyPacket.Close close = msg.asClose();
                LOGGER.debug("ProxyPacket.close:UserCode:{}", close.getUserCode());
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

    protected SharaChannels sharaChannels() {
        return sharaChannels;
    }


    protected void channelReadAuth(BTPChannel btpChannel, ProxyPacket.Auth msg) {
        SharaChannels sharaChannels = sharaChannels();
        sharaChannels.putTcpBtpChannel(msg.getAuthCode(), btpChannel);
        btpChannel.writeAuth(msg.getAuthCode());
    }

    protected void channelReadConnected(BTPChannel btpChannel, ProxyPacket.Connected msg) {
        NaturalChannel naturalChannel = btpChannel.getNaturalChannel(msg.getUserCode());
        naturalChannel.realConnected();
    }

    protected void channelReadTransmit(BTPChannel btpChannel, ProxyPacket.Transmit msg) {
        NaturalChannel naturalChannel =
            btpChannel.getNaturalChannel(msg.getUserCode());
        naturalChannel.writeMsgAndFlush(msg.getBody());
        LOGGER.debug("channelReadTransmit userCode: {}, naturalChannel: {}", msg.getUserCode(), naturalChannel);
    }

    protected void channelReadClose(BTPChannel btpChannel, ProxyPacket.Close msg) {
        NaturalChannel naturalChannel =
            btpChannel.getNaturalChannel(msg.getUserCode());
        naturalChannel.flushAndClose();
    }
}
