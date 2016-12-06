package zzy.zyproxy.core.channel;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public abstract class NaturalChannel extends ProxyChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NaturalChannel.class);
    private BTPChannel btpChannel;
    private Runnable realConnectedEvent;
    private String userCode;

    public NaturalChannel() {
        this(null);
    }

    public NaturalChannel(ChannelHandlerContext ctx) {
        super(ctx);
        if (ctx != null) {
            flushUserCode(ctx.channel());
        }
    }

    public void flushBTPChannel(BTPChannel btpChannel) {
        this.btpChannel = btpChannel;
    }

    public String userCode() {
        return userCode;
    }

    private void flushUserCode(Channel channel) {
        userCode = String.valueOf(channel.hashCode());
    }

    @Override
    public void flushChannelHandlerContext(ChannelHandlerContext ctx) {
        super.flushChannelHandlerContext(ctx);
        flushUserCode(ctx.channel());
    }

    public abstract ChannelFuture writeToBTPChannelConnected();

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
}
