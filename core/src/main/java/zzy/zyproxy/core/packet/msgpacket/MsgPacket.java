package zzy.zyproxy.core.packet.msgpacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.codec.msgpack.MsgpackPacket;
import zzy.zyproxy.core.packet.ProxyPacket;

/**
 * @author zhouzhongyuan
 * @date 2016/12/2
 */
public class MsgPacket implements ProxyPacket, MsgpackPacket {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgPacket.class);

}
