package zzy.zyproxy.core.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public interface InboundHandlerEvent<I> {

    void channelRegisteredEvent(ChannelHandlerContext ctx) throws Exception;

    void channelUnregisteredEvent(ChannelHandlerContext ctx) throws Exception;

    void channelActiveEvent(ChannelHandlerContext ctx) throws Exception;

    void channelInactiveEvent(ChannelHandlerContext ctx) throws Exception;

    void channelReadEvent(ChannelHandlerContext ctx, I msg) throws Exception;

    void channelReadCompleteEvent(ChannelHandlerContext ctx) throws Exception;

    void userEventTriggeredEvent(ChannelHandlerContext ctx, Object evt) throws Exception;

    void channelWritabilityChangedEvent(ChannelHandlerContext ctx) throws Exception;

    void exceptionCaughtEvent(ChannelHandlerContext ctx, Throwable cause) throws Exception;
}
