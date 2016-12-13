package zzy.zyproxy.netnat.util;

import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.packet.msgpacket.MsgPacket;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public abstract class ProxyPacketFactory {
    /**
     * @return 返回代理的信息
     */
    public static ProxyPacket newProxyPacket() {
        return new MsgPacket();
    }

    public static ProxyPacket newPacketAuth(String authCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Auth auth = proxyPacket.newAuth();
        auth.setAuthCode(authCode);
        return proxyPacket;
    }

    public static ProxyPacket newPacketConnected(Integer userCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Connected connected = proxyPacket.newConnected();
        connected.setUserCode(userCode);
        return proxyPacket;
    }

    public static ProxyPacket newPacketTransmit(Integer userCode, byte[] msgBody) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Transmit transmit = proxyPacket.newTransmit();
        transmit.setUserCode(userCode);
        transmit.setBody(msgBody);
        return proxyPacket;
    }

    public static ProxyPacket newPacketClose(Integer userCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Close close = proxyPacket.newClose();
        close.setUserCode(userCode);
        return proxyPacket;
    }
}
