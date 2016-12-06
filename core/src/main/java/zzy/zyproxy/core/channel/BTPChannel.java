package zzy.zyproxy.core.channel;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;

import java.util.HashMap;

/**
 * @author zhouzhongyuan
 * @date 2016/12/2
 */
public abstract class BTPChannel extends ProxyChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(BTPChannel.class);
    private HashMap<String, NaturalChannel> naturalChannelMap
        = new HashMap<String, NaturalChannel>();

    protected ChannelFuture writeMsgAndFlush(ProxyPacket msg) {
        return super.writeAndFlush(msg);
    }

    public abstract ChannelFuture writeAuth(String authCode);

    public abstract ChannelFuture writeConnected(String userCode);

    public abstract ChannelFuture writeTransmit(String userCode, byte[] msgBody);

    public abstract ChannelFuture writeClose(String userCode);

    public NaturalChannel getNaturalChannel(String userCode) {
        return naturalChannelMap.get(userCode);
    }

    public NaturalChannel putNaturalChannel(String userCode, NaturalChannel naturalChannel) {
        return naturalChannelMap.put(userCode, naturalChannel);
    }
    
    

}
