package zzy.zyproxy.netnat.channel;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jboss.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public abstract class HeartChannel {
    protected Channel channel;

    /**
     * channel 可以是null 使用getHeartChannel()可以进行channel的更新
     *
     * @param channel
     */
    public HeartChannel(Channel channel) {
        this.channel = channel;
    }

    public void close() {
        channel.close();
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
        return channel.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HeartChannel) {
            HeartChannel heartChannelobj = (HeartChannel) obj;
            return channel.equals(heartChannelobj.getChannel());
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

    public abstract HeartChannel getHeartByChannel(Channel channel);


}
