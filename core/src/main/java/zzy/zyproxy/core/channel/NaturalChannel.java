package zzy.zyproxy.core.channel;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public abstract class NaturalChannel extends ProxyChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NaturalChannel.class);
    private BTPChannel btpChannel;
    protected Runnable realConnectedEvent;
    private final String userCode;

    public NaturalChannel(String userCode) {
        this(null, userCode);
    }

    public NaturalChannel(ChannelHandlerContext ctx, String userCode) {
        super(ctx);
        this.userCode = userCode;
    }

    public void flushBTPChannel(BTPChannel btpChannel) {
        this.btpChannel = btpChannel;
    }

    @Override
    public void flushChannelHandlerContext(ChannelHandlerContext ctx) {
        super.flushChannelHandlerContext(ctx);
    }

    public String userCode() {
        return userCode;
    }

    public abstract ChannelFuture writeToBTPChannelConnected(Runnable realConnectedEvent);

    public abstract ChannelFuture writeToBTPChannelTransmit(byte[] msgBody);

    public abstract ChannelFuture writeToBTPChannelClose();

    public BTPChannel BTPChannel() {
        return btpChannel;
    }

    //========================
    //从BTPChannel处理调用的方法
    //========================
    public ChannelFuture closeChannel() {
        return flushAndClose();
    }

    public ChannelFuture writeMsgAndFlush(byte[] msg) {
        return super.writeAndFlush(Unpooled.wrappedBuffer(msg));
    }

    public void realConnected() {
        if (realConnectedEvent != null) {
            realConnectedEvent.run();
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("btpChannel", btpChannel)
                .append("realConnectedEvent", realConnectedEvent)
                .append("userCode", userCode)
                .toString();
    }
}
