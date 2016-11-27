package zzy.zyproxy.netnat.natsrv.channel;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.channel.HeartChannel;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class NatBackChannel extends HeartChannel {

    public NatBackChannel(Channel channel) {
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
        return new NatBackChannel(channel);
    }


    public ChannelFuture writeRegisterLanBack(InetSocketAddress acptUserAddr) {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new NatRegisterBTPChannel().setAcptUserPort(acptUserAddr.getPort()));
        return channel.write(msg);
    }
}
