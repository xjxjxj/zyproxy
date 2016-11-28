package zzy.zyproxy.netnat.channel;

import org.jboss.netty.channel.Channel;
import zzy.zyproxy.core.channel.ProxyChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public abstract class HeartChannel<T> extends ProxyChannel {

    /**
     * channel 可以是null 使用getHeartChannel()可以进行channel的更新
     *
     * @param channel
     */
    public HeartChannel(Channel channel) {
        super(channel);
    }
}
