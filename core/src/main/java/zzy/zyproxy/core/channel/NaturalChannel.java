package zzy.zyproxy.core.channel;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public abstract class NaturalChannel extends ProxyChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NaturalChannel.class);
    private BTPChannel btpChannel;

    protected ChannelFuture writeAndFlush(byte[] msg) {
        return channel().writeAndFlush(Unpooled.wrappedBuffer(msg));
    }

    public void flushBTPChannel(BTPChannel btpChannel) {
        this.btpChannel = btpChannel;
    }

    public abstract ChannelFuture writeToBTPChannelConnected(String userCode);

    public abstract ChannelFuture writeToBTPChannelTransmit(String userCode, byte[] msgBody);

    public abstract ChannelFuture writeToBTPChannelClose(String userCode);

    public BTPChannel BTPChannel() {
        return btpChannel;
    }
}
