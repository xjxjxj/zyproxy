package zzy.zyproxy.netnat.util;

import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.packet.msgpacket.MsgPacket;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public abstract class ProxyPacketFactory {
    /**
     * TODO config
     * @return 返回代理的信息
     */
    public static ProxyPacket newProxyPacket() {
        return new MsgPacket();
    }
}
