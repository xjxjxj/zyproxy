package zzy.zyproxy.netnat.util;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.handler.InboundHandlerEvent;
import zzy.zyproxy.core.util.SubLogger;
import zzy.zyproxy.core.util.task.TaskExecutor;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public abstract class AbstractInboundHandlerEvent<I> implements InboundHandlerEvent<I>, SubLogger {

    public abstract TaskExecutor taskExecutor();

    public final void channelRegisteredEvent(ChannelHandlerContext ctx) {
        Runnable runnable = channelRegisteredTask(ctx);
        if (runnable != null) {
            taskExecutor().submitQueueTask(runnable);
        }
    }

    protected Runnable channelRegisteredTask(ChannelHandlerContext ctx) {
        return null;
    }

    public final void channelUnregisteredEvent(ChannelHandlerContext ctx) {
        Runnable runnable = channelUnregisteredTask();
        if (runnable != null) {
            taskExecutor().submitQueueTask(runnable);
        }
    }

    protected Runnable channelUnregisteredTask() {
        return null;
    }

    public final void channelActiveEvent(ChannelHandlerContext ctx) {
        Runnable runnable = channelActiveTask(ctx);
        if (runnable != null) {
            taskExecutor().submitQueueTask(runnable);
        }
    }

    protected Runnable channelActiveTask(ChannelHandlerContext ctx) {
        return null;
    }

    public final void channelInactiveEvent(ChannelHandlerContext ctx) {
        Runnable runnable = channelInactiveTask(ctx);
        if (runnable != null) {
            taskExecutor().submitQueueTask(runnable);
        }
    }

    protected Runnable channelInactiveTask(ChannelHandlerContext ctx) {
        return null;
    }

    public final void channelReadEvent(ChannelHandlerContext ctx, I msg) {
        Runnable runnable = channelReadTask(ctx, msg);
        if (runnable != null) {
            taskExecutor().submitQueueTask(runnable);
        }
    }

    protected Runnable channelReadTask(ChannelHandlerContext ctx, I msg) {
        return null;
    }

    public final void channelReadCompleteEvent(ChannelHandlerContext ctx) {
        Runnable runnable = channelReadCompleteTask(ctx);
        if (runnable != null) {
            taskExecutor().submitQueueTask(runnable);
        }
    }

    protected Runnable channelReadCompleteTask(ChannelHandlerContext ctx) {
        return null;
    }

    public final void userEventTriggeredEvent(ChannelHandlerContext ctx, Object evt) {
        Runnable runnable = userEventTriggeredTask(ctx, evt);
        if (runnable != null) {
            taskExecutor().submitQueueTask(runnable);
        }
    }

    protected Runnable userEventTriggeredTask(ChannelHandlerContext ctx, Object evt) {
        return null;
    }

    public final void channelWritabilityChangedEvent(ChannelHandlerContext ctx) {
        Runnable runnable = channelWritabilityChangedTask(ctx);
        if (runnable != null) {
            taskExecutor().submitQueueTask(runnable);
        }
    }

    protected Runnable channelWritabilityChangedTask(ChannelHandlerContext ctx) {
        return null;
    }

    public final void exceptionCaughtEvent(ChannelHandlerContext ctx, Throwable cause) {
        Runnable runnable = exceptionCaughtTask(ctx, cause);
        if (runnable != null) {
            taskExecutor().submitQueueTask(runnable);
        }
    }

    protected Runnable exceptionCaughtTask(ChannelHandlerContext ctx, Throwable cause) {
        return null;
    }
}
