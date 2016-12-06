package zzy.zyproxy.netnat.nat.handler;

import io.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.handler.BTPInboundHandler;
import zzy.zyproxy.core.packet.ProxyPacket;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public class NatBTPHandler extends BTPInboundHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPHandler.class);
    private final String auth;

    public NatBTPHandler(BTPChannel btpChannel, String auth) {
        super(btpChannel);
        this.auth = auth;
    }

    protected void channelReadAuth(BTPChannel btpChannel, ProxyPacket.Auth msg) {
    }

    protected void channelReadConnected(BTPChannel btpChannel, ProxyPacket.Connected msg) {
    }

    protected void channelReadTransmit(BTPChannel btpChannel, ProxyPacket.Transmit msg) {
    }

    protected void channelReadClose(BTPChannel btpChannel, ProxyPacket.Close msg) {
    }
}
