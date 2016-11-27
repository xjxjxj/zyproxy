package zzy.zyproxy.core.util;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;

/**
 * Created by zhou on 2016/11/26.
 */
public abstract class ChannelPiplineUtil {
    public static void addLast(ChannelPipeline channelPipeline, ChannelHandler... channelHandlers) {
        for (ChannelHandler channelHandler : channelHandlers) {
            channelPipeline.addLast(channelHandler.getClass().getName(), channelHandler);
        }
    }
}
