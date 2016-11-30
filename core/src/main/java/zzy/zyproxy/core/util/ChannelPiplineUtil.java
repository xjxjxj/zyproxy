package zzy.zyproxy.core.util;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;

/**
 * Created by zhou on 2016/11/26.
 */
public abstract class ChannelPiplineUtil {
    public static void addLast(ChannelPipeline channelPipeline, ChannelHandler... channelHandlers) {
        for (ChannelHandler channelHandler : channelHandlers) {
            channelPipeline.addLast(channelHandler.getClass().getName(), channelHandler);
        }
    }

    public static void addInfo(ChannelPipeline channelPipeline) {
//        channelPipeline.addLast("logger", new LoggingHandler(InternalLogLevel.ERROR));
    }
}
