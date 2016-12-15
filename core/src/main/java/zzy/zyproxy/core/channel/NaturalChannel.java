package zzy.zyproxy.core.channel;

import io.netty.channel.ChannelFuture;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public interface NaturalChannel extends ProxyChannelHandlerContext {

    Integer userCode();

    BTPChannel btpChannel();

    ChannelFuture writeMsgAndFlush(byte[] body);

    //==
    void submitTask(Runnable task);

    //==
    void channelActive();

    void channelRead(byte[] bytes);

    void channelInactive();

    void channelWritabilityChanged();
}
