package zzy.zyproxy.core.channel;

import io.netty.channel.Channel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public abstract class ProxyChannel {
    private Channel channel;

    public ProxyChannel() {
        this(null);
    }

    public ProxyChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel channel() {
        return channel;
    }

    public void flushChannel(Channel channel) {
        this.channel = channel;
    }
    
}
