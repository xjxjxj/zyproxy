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
public class NatHeartChannel extends HeartChannel<NatHeartChannel> {

    public NatHeartChannel(Channel channel) {
        super(channel);
    }

    public NatHeartChannel flushChannel(Channel channel) {
        super.flushChannel0(channel);
        return this;
    }

    public ChannelFuture writeRegisterLanHeart(InetSocketAddress lanProxyAddr) {
        HeartMsg heartMsg = new HeartMsg();
        heartMsg.setHeartBody(heartMsg.new NatRegisterHeart().setNetAcptUserPort(lanProxyAddr.getPort()));
        return channel.write(heartMsg);
    }

    public ChannelFuture writeNatResponseNewChannel() {
        HeartMsg heartMsg = new HeartMsg();
        heartMsg.setHeartBody(heartMsg.new NatResponseBTPChannel());
        return channel.write(heartMsg);
    }

    public ChannelFuture writePing() {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new Ping());
        return channel.write(msg);
    }
}
