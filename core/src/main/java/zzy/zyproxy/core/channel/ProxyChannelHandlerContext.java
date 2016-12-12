package zzy.zyproxy.core.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhouzhongyuan
 * @date 2016/12/12
 */
public interface ProxyChannelHandlerContext {
    ChannelFuture flushAndClose();

    void ctxRead();

    ChannelFuture writeAndFlush(Object msg);

    void flushChannelHandlerContext(ChannelHandlerContext ctx);

    ChannelHandlerContext channelHandlerContext();

    boolean ctxchannelIsActive();
}
