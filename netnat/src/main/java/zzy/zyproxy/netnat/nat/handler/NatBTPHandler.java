package zzy.zyproxy.netnat.nat.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.handler.BTPInboundHandler;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.netnat.channel.NatNaturalChannel;
import zzy.zyproxy.netnat.nat.RealClientFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public class NatBTPHandler extends BTPInboundHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPHandler.class);
    private final String auth;
    private final RealClientFactory realClientFactory;

    public NatBTPHandler(BTPChannel btpChannel, String auth, RealClientFactory realClientFactory) {
        super(btpChannel);
        this.auth = auth;
        this.realClientFactory = realClientFactory;
    }

    protected void active(BTPChannel btpChannel) {
        btpChannel.writeAuth(auth);
    }

    protected void channelReadAuth(BTPChannel btpChannel, ProxyPacket.Auth msg) {
        if (!auth.equals(msg.getAuthCode())) {
            btpChannel.flushAndClose();
        }
    }

    protected void channelReadConnected(BTPChannel btpChannel, ProxyPacket.Connected msg) {
        NatNaturalChannel natNaturalChannel = new NatNaturalChannel(msg.getUserCode());
        btpChannel.putNaturalChannel(msg.getUserCode(), natNaturalChannel);
        natNaturalChannel.flushBTPChannel(btpChannel);
        realClientFactory.createClient(natNaturalChannel);
    }

    protected void channelReadTransmit(BTPChannel btpChannel, ProxyPacket.Transmit msg) {
        NaturalChannel naturalChannel = btpChannel.getNaturalChannel(msg.getUserCode());
        naturalChannel.writeMsgAndFlush(msg.getBody());
    }

    protected void channelReadClose(BTPChannel btpChannel, ProxyPacket.Close msg) {
        NaturalChannel naturalChannel = btpChannel.getNaturalChannel(msg.getUserCode());
        naturalChannel.flushAndClose();
    }
}
