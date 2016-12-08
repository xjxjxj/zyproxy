package zzy.zyproxy.core.channel;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public class ProxyChannel {
    private ChannelHandlerContext ctx;

    public ProxyChannel() {
        this(null);
    }

    public ProxyChannel(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public ChannelHandlerContext channelHandlerContext() {
        return ctx;
    }

    public void flushChannelHandlerContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    protected ChannelFuture writeAndFlush(Object msg) {
        return ctx.writeAndFlush(msg);
    }

    public ChannelFuture flushAndClose() {
        return writeAndFlush(Unpooled.EMPTY_BUFFER)
            .addListener(ChannelFutureListener.CLOSE);
    }

    public void ctxRead() {
        ctx.read();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProxyChannel that = (ProxyChannel) o;

        return ctx != null ? ctx.equals(that.ctx) : that.ctx == null;
    }

    @Override
    public int hashCode() {
        return ctx != null ? ctx.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ProxyChannel{" +
            "ctx=" + ctx +
            '}';
    }
}
