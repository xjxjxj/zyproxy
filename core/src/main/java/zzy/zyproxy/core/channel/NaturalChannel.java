package zzy.zyproxy.core.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public interface NaturalChannel {
    
    Integer userCode();

    ChannelFuture writeToBTPChannelConnected();

    ChannelFuture writeToBTPChannelTransmit(byte[] msgBody);

    ChannelFuture writeToBTPChannelClose();

    BTPChannel BTPChannel();

    void ctxRead();

    void regConnectedEvent(Runnable connectedEvent);

    ChannelFuture flushAndClose();

    void realConnected();

    ChannelFuture writeMsgAndFlush(byte[] body);

    void flushChannelHandlerContext(ChannelHandlerContext ctx);
}
