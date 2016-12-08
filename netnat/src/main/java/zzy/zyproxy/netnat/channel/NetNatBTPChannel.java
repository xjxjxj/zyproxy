package zzy.zyproxy.netnat.channel;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.channel.ProxyChannel;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.netnat.util.ProxyPacketFactory;

import java.util.HashMap;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class NetNatBTPChannel extends ProxyChannel implements BTPChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NetNatBTPChannel.class);

    private final HashMap<Integer, NaturalChannel> naturalChannelHashMap
        = new HashMap<Integer, NaturalChannel>();

    public ChannelFuture writeMsgAndFlush(ProxyPacket msg) {
        return super.writeAndFlush(msg);
    }

    public ChannelFuture writeAuth(String authCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Auth auth = proxyPacket.newAuth();
        auth.setAuthCode(authCode);
        return writeMsgAndFlush(proxyPacket);
    }

    public ChannelFuture writeConnected(Integer userCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Connected connected = proxyPacket.newConnected();
        connected.setUserCode(userCode);
        return writeMsgAndFlush(proxyPacket);
    }

    public ChannelFuture writeTransmit(Integer userCode, byte[] msgBody) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Transmit transmit = proxyPacket.newTransmit();
        transmit.setUserCode(userCode);
        transmit.setBody(msgBody);
        return writeMsgAndFlush(proxyPacket);
    }

    public ChannelFuture writeClose(Integer userCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Close close = proxyPacket.newClose();
        close.setUserCode(userCode);
        return writeMsgAndFlush(proxyPacket);
    }

    public NaturalChannel getNaturalChannel(Integer userCode) {
        return naturalChannelHashMap.get(userCode);
    }

    public NaturalChannel putNaturalChannel(Integer userCode, NaturalChannel naturalChannel) {
        return naturalChannelHashMap.put(userCode, naturalChannel);
    }

}
