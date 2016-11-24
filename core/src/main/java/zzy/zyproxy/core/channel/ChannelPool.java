package zzy.zyproxy.core.channel;

import zzy.zyproxy.core.channel.FrontToBackProxyChannel;

import java.util.concurrent.TimeUnit;

/**
 * @author zhouzhongyuan
 * @date 2016/11/23
 */
public interface ChannelPool {

    FrontToBackProxyChannel takeFrontToBackChannel(long timeout, TimeUnit unit) throws InterruptedException;

    boolean putFrontToBackChannel(FrontToBackProxyChannel channel, long timeout, TimeUnit unit)
        throws InterruptedException;
}
