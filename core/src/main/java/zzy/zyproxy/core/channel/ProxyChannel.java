package zzy.zyproxy.core.channel;

import org.jboss.netty.channel.Channel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/23
 */
public interface ProxyChannel {
    Channel getChannel();
}
