package zzy.zyproxy.netlan.lansrv.channel;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netlan.channel.HeartChannel;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class LanHeartChannel extends HeartChannel {

    public LanHeartChannel(Channel channel) {
        super(channel);
    }

    public HeartChannel getHeartByChannel(Channel channel) {
        if (this.channel == null){
            this.channel = channel;
            return this;
        }
        if (this.channel.equals(channel)) {
            return this;
        }
        return new LanHeartChannel(channel);
    }

    public ChannelFuture writeRegisterLanHeart(InetSocketAddress lanProxyAddr) {
        HeartMsg heartMsg = new HeartMsg();
        heartMsg.setHeartBody(heartMsg.new RegisterLanHeart().setProxyPort(lanProxyAddr.getPort()));
        return channel.write(heartMsg);
    }
    public ChannelFuture writePing() {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new Ping());
        return channel.write(msg);
    }
}
