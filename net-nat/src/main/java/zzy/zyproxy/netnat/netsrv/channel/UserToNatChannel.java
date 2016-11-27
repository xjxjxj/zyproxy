package zzy.zyproxy.netnat.netsrv.channel;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jboss.netty.channel.Channel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class UserToNatChannel {
    private final Channel acptUserchannel;
    private final NatChannel lanToBackChannel;

    public UserToNatChannel(Channel acptUserchannel, NatChannel lanToBackChannel) {
        this.acptUserchannel = acptUserchannel;
        this.lanToBackChannel = lanToBackChannel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("acptUserchannel", acptUserchannel)
                .append("lanToBackChannel", lanToBackChannel)
                .toString();
    }
}
