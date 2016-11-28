package zzy.zyproxy.core.util;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author zhouzhongyuan
 * @date 2016/11/28
 */
public class ChannelFutureNull implements ChannelFuture{
    private final static Logger LOGGER = LoggerFactory.getLogger(ChannelFutureNull.class);

    public Channel getChannel() {
        return null;
    }

    public boolean isDone() {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isSuccess() {
        return false;
    }

    public Throwable getCause() {
        return null;
    }

    public boolean cancel() {
        return false;
    }

    public boolean setSuccess() {
        return false;
    }

    public boolean setFailure(Throwable cause) {
        return false;
    }

    public boolean setProgress(long amount, long current, long total) {
        return false;
    }

    public void addListener(ChannelFutureListener listener) {

    }

    public void removeListener(ChannelFutureListener listener) {

    }

    public ChannelFuture sync() throws InterruptedException {
        return null;
    }

    public ChannelFuture syncUninterruptibly() {
        return null;
    }

    public ChannelFuture await() throws InterruptedException {
        return null;
    }

    public ChannelFuture awaitUninterruptibly() {
        return null;
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public boolean await(long timeoutMillis) throws InterruptedException {
        return false;
    }

    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return false;
    }

    public boolean awaitUninterruptibly(long timeoutMillis) {
        return false;
    }
}
