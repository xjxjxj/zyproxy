package zzy.zyproxy.core.channel;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/23
 */
public abstract class ProxyChannel<T> {
    protected Channel channel;

    /**
     * channel 可以是null 使用getHeartChannel()可以进行channel的更新
     *
     * @param channel 代理的channel
     */
    public ProxyChannel(Channel channel) {
        this.channel = channel;
    }

    public ChannelFuture disconnect() {
        if (channel != null && channel.isConnected()) {
            return channel.disconnect();
        }
        return null;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("channel", channel)
            .toString();
    }

    @Override
    public int hashCode() {
        if (channel != null) {
            return channel.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProxyChannel) {
            ProxyChannel heartChannelobj = (ProxyChannel) obj;
            if (channel != null) {
                return channel.equals(heartChannelobj.getChannel());
            }
        }
        return super.equals(obj);
    }

    public Integer getLocalAddressPort() {
        SocketAddress localAddress = channel.getLocalAddress();
        if (localAddress instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) localAddress;
            return address.getPort();
        }
        return null;
    }

    public Boolean isConnected() {
        return channel.isConnected();
    }

    protected void flushChannel0(Channel channel) {
        this.channel = channel;
    }

    public abstract T flushChannel(Channel channel);
}
