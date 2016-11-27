package zzy.zyproxy.netnat.netsrv.channel;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import zzy.zyproxy.core.packet.heart.HeartMsg;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class UserNatBTPChannel {
    private final Channel acptUserchannel;
    private final NatBTPChannel natBTPChannel;

    public UserNatBTPChannel(Channel acptUserchannel, NatBTPChannel natBTPChannel) {
        this.acptUserchannel = acptUserchannel;
        this.natBTPChannel = natBTPChannel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("acptUserchannel", acptUserchannel)
                .append("natBTPChannel", natBTPChannel)
                .toString();
    }

    public ChannelFuture userWriteToNatBTP(byte[] bytes) {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new UserWriteToNatBTP().setMsgBody(bytes));
        return natBTPChannel.write(msg);
    }
}
