package zzy.zyproxy.netnat.netsrv.channel;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import zzy.zyproxy.core.channel.ProxyChannel;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.channel.HeartChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class UserNatBTPChannel {
    private UserChannel userchannel;
    private NatBTPChannel natBTPChannel;

    public UserNatBTPChannel(Channel userchannel, Channel natBTPChannel) {
        this.userchannel = new UserChannel(userchannel);
        this.natBTPChannel = new NatBTPChannel(natBTPChannel);
    }

    public NatBTPChannel flushNatBTPChannel(Channel channel) {
        natBTPChannel = natBTPChannel.flushChannel(channel);
        return natBTPChannel;
    }

    public UserChannel flushUserChannel(Channel channel) {
        userchannel = userchannel.flushChannel(channel);
        return userchannel;
    }

    public UserNatBTPChannel flushUserNatBTPChannel(Channel userChannel, Channel natBTPChannel) {
        this.userchannel = flushUserChannel(userChannel);
        this.natBTPChannel = flushNatBTPChannel(natBTPChannel);
        return this;
    }

    public void close() {
        userchannel.close();
        natBTPChannel.close();
    }

    public class UserChannel extends ProxyChannel<UserChannel> {
        public UserChannel(Channel channel) {
            super(channel);
        }

        @Override
        public UserChannel flushChannel(Channel channel) {
            this.channel = channel;
            return this;
        }

        public ChannelFuture writeToNatBTP(byte[] bytes) {
            HeartMsg msg = new HeartMsg();
            msg.setHeartBody(msg.new UserWriteToNatBTP().setMsgBody(bytes));
            return natBTPChannel.write(msg);
        }

        public ChannelFuture channelConnected() {
            HeartMsg msg = new HeartMsg();
            msg.setHeartBody(msg.new UserChannelConnected());
            return natBTPChannel.write(msg);
        }
    }

    public class NatBTPChannel extends HeartChannel<NatBTPChannel> {
        public NatBTPChannel(Channel channel) {
            super(channel);
        }

        @Override
        public NatBTPChannel flushChannel(Channel channel) {
            this.channel = channel;
            return this;
        }

        public ChannelFuture write(HeartMsg msg) {
            return this.channel.write(msg);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("userchannel", userchannel)
            .append("natBTPChannel", natBTPChannel)
            .toString();
    }
}
