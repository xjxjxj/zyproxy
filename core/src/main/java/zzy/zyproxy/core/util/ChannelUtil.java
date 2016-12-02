package zzy.zyproxy.core.util;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.Channels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/12/1
 */
public class ChannelUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChannelUtil.class);

    public static void closeOnFlush(final Channel channel) {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.setReadable(false).addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        channel.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                    }
                });
            } else {
                Channels.close(channel);
            }
        }
    }
}
