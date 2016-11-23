package zzy.zyproxy.base;

/**
 * @author zhouzhongyuan
 * @date 2016/11/23
 */
public interface ProxyChannel {
    org.jboss.netty.channel.Channel getChannel();
}
