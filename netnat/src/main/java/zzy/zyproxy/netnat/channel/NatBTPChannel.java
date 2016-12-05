package zzy.zyproxy.netnat.channel;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.netnat.util.ProxyPacketFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class NatBTPChannel extends BTPChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPChannel.class);

    public ChannelFuture writeAuth(String authCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Auth auth = proxyPacket.newAuth();
        auth.setAuthCode(authCode);
        return writeAndFlush(proxyPacket);
    }

    public ChannelFuture writeConnected(String userCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Connected connected = proxyPacket.newConnected();
        connected.setUserCode(userCode);
        return writeAndFlush(proxyPacket);
    }

    public ChannelFuture writeTransmit(String userCode, byte[] msgBody) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Transmit transmit = proxyPacket.newTransmit();
        transmit.setUserCode(userCode);
        transmit.setBody(msgBody);
        return writeAndFlush(proxyPacket);
    }

    public ChannelFuture writeClose(String userCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Close close = proxyPacket.newClose();
        close.setUserCode(userCode);
        return writeAndFlush(proxyPacket);
    }
}
