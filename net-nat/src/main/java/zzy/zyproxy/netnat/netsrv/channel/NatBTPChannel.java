package zzy.zyproxy.netnat.netsrv.channel;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.channel.HeartChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class NatBTPChannel extends HeartChannel{

    public NatBTPChannel(Channel channel) {
        super(channel);
    }


    public HeartChannel getHeartByChannel(Channel channel) {
        if (this.channel == null) {
            this.channel = channel;
            return this;
        }
        if (this.channel.equals(channel)) {
            return this;
        }
        return new NatBTPChannel(channel);
    }

    public ChannelFuture write(HeartMsg msg) {
        return channel.write(msg);
    }
}
