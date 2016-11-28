package zzy.zyproxy.netnat.natsrv.channel;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import zzy.zyproxy.core.channel.ProxyChannel;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.channel.HeartChannel;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class RealNatBTPChannel {

    private NatBTPChannel natBTPChannel;
    private RealChannel realChannel;

    public RealNatBTPChannel(Channel realChannel, Channel natBTPChannel) {
        this.realChannel = new RealChannel(realChannel);
        this.natBTPChannel = new NatBTPChannel(natBTPChannel);
    }

    public NatBTPChannel flushNatBTPChannel(Channel natBTPChannel) {
        this.natBTPChannel = this.natBTPChannel.flushChannel(natBTPChannel);
        return this.natBTPChannel;
    }

    public RealChannel flushRealChannel(Channel realChannel) {
        this.realChannel = this.realChannel.flushChannel(realChannel);
        return this.realChannel;
    }


    public RealNatBTPChannel flushRealNatBTPChannel(Channel realChannel, Channel natBTPChannel) {
        this.natBTPChannel = flushNatBTPChannel(natBTPChannel);
        this.realChannel = flushRealChannel(realChannel);
        return this;
    }


    public ChannelFuture writeRegisterNatBTP(InetSocketAddress acptUserAddr) {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new NatRegisterBTPChannel().setAcptUserPort(acptUserAddr.getPort()));
        return natBTPChannel.write(msg);
    }

    public ChannelFuture writeRealChannelConnected() {
        return natBTPChannel.writeRealChannelConnected();
    }

    public class RealChannel extends ProxyChannel<RealChannel> {
        public RealChannel(Channel channel) {
            super(channel);
        }

        @Override
        public RealChannel flushChannel(Channel channel) {
            super.flushChannel0(channel);
            return this;
        }

    }

    public class NatBTPChannel extends HeartChannel<NatBTPChannel> {
        public NatBTPChannel(Channel channel) {
            super(channel);
        }

        @Override
        public NatBTPChannel flushChannel(Channel channel) {
            super.flushChannel0(channel);
            return this;
        }

        public ChannelFuture write(HeartMsg msg) {
            return this.channel.write(msg);
        }

        public ChannelFuture writeRealChannelConnected() {
            HeartMsg msg = new HeartMsg();
            msg.setHeartBody(msg.new RealChannelConnected());
            return write(msg);
        }
    }
}
