package zzy.zyproxy.netnat.netsrv.channel;

import org.jboss.netty.channel.Channel;
import zzy.zyproxy.netnat.channel.HeartChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class NatChannel extends HeartChannel{

    public NatChannel(Channel channel) {
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
        return new NatChannel(channel);
    }
}
