package zzy.zyproxy.core.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public interface NaturalChannel {

    Integer userCode();

    BTPChannel btpChannel();

    void ctxRead();

    void regConnectedEvent(Runnable connectedEvent);

    void triggerConnectedEvent();

    ChannelFuture flushAndClose();

    ChannelFuture writeMsgAndFlush(byte[] body);

    void flushChannelHandlerContext(ChannelHandlerContext ctx);

    void channelActive();

    void channelRead(byte[] bytes);

    void channelInactive();

    void channelWritabilityChanged();
}
