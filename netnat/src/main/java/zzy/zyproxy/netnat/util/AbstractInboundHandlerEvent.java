package zzy.zyproxy.netnat.util;

import io.netty.channel.ChannelHandlerContext;
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
        
    }
    

    public final void channelUnregisteredEvent(ChannelHandlerContext ctx) {
        
    }


    public void channelActiveEvent(ChannelHandlerContext ctx) {
        
    }


    public void channelInactiveEvent(ChannelHandlerContext ctx) {
        
    }


    public void channelReadEvent(ChannelHandlerContext ctx, I msg) {
        
    }


    public final void channelReadCompleteEvent(ChannelHandlerContext ctx) {
        
    }


    public final void userEventTriggeredEvent(ChannelHandlerContext ctx, Object evt) {
        
    }


    public final void channelWritabilityChangedEvent(ChannelHandlerContext ctx) {
        
    }


    public final void exceptionCaughtEvent(ChannelHandlerContext ctx, Throwable cause) {
        
    }

}
