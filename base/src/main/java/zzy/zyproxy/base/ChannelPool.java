package zzy.zyproxy.base;

import zzy.zyproxy.base.channel.FrontToBackProxyChannel;

import java.util.concurrent.BlockingQueue;
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
