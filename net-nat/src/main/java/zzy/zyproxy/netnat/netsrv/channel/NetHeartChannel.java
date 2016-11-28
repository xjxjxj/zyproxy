package zzy.zyproxy.netnat.netsrv.channel;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.channel.HeartChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class NetHeartChannel extends HeartChannel<NetHeartChannel> {


    public NetHeartChannel(Channel channel) {
        super(channel);
    }

    public NetHeartChannel flushChannel(Channel channel) {
        super.flushChannel0(channel);
        return this;
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
