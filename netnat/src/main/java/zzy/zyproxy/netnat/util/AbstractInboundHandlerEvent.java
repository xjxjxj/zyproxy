package zzy.zyproxy.netnat.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import zzy.zyproxy.core.handler.InboundHandlerEvent;
import zzy.zyproxy.core.util.SubLogger;
import zzy.zyproxy.core.util.task.TaskExecutor;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public abstract class AbstractInboundHandlerEvent<I> implements InboundHandlerEvent<I>, SubLogger {

    public abstract TaskExecutor taskExecutor();

    public void channelRegisteredEvent(ChannelHandlerContext ctx) {

    }


    public void channelUnregisteredEvent(ChannelHandlerContext ctx) {

    }


    public void channelActiveEvent(ChannelHandlerContext ctx) {

    }


    public void channelInactiveEvent(ChannelHandlerContext ctx) {

    }


    public void channelReadEvent(ChannelHandlerContext ctx, I msg) {

    }


    public void channelReadCompleteEvent(ChannelHandlerContext ctx) {

    }


    public void userIdleStateEvent(ChannelHandlerContext ctx, IdleStateEvent evt) {

    }


    public void channelWritabilityChangedEvent(ChannelHandlerContext ctx) {

    }


    public void exceptionCaughtEvent(ChannelHandlerContext ctx, Throwable cause) {

    }

}
