package zzy.zyproxy.netnat.netsrv.channel;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.channel.HeartChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class NetHeartChannel extends HeartChannel {


    public NetHeartChannel(Channel channel) {
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
        return new NetHeartChannel(channel);
    }

    public ChannelFuture writePong() {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new Pong());
        return channel.write(msg);
    }

    public ChannelFuture writeReqNatChannel() {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new NetRequestBTPChannel());
        return channel.write(msg);
    }
}
