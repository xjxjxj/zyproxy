package zzy.zyproxy.core.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;

/**
 * @author zhouzhongyuan
 * @date 2016/12/2
 * PT ProxyPacket Type
 */
public abstract class BTPChannel<PT extends ProxyPacket> {
    private final static Logger LOGGER = LoggerFactory.getLogger(BTPChannel.class);
    private Channel channel;

    public BTPChannel() {
        this(null);
    }

    public BTPChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel channel() {
        return channel;
    }

    public BTPChannel<PT> flushChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    protected ChannelFuture writeAndFlush(PT msg) {
        return channel().writeAndFlush(msg);
    }

}
