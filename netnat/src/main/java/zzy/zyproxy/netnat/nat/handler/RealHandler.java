package zzy.zyproxy.netnat.nat.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.NaturalChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/7
 */
public class RealHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(RealHandler.class);
    private volatile NaturalChannel naturalChannel;

    public RealHandler(NaturalChannel naturalChannel) {
        super();
        if (naturalChannel == null) {
            throw new NullPointerException("RealHandler#NaturalChannel");
        }
        this.naturalChannel = naturalChannel;
    }


    private NaturalChannel flushNaturalChannel(ChannelHandlerContext ctx) {
        naturalChannel.flushChannelHandlerContext(ctx);
        return naturalChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        flushNaturalChannel(ctx).channelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            return;
        }
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        flushNaturalChannel(ctx).channelRead(bytes);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        flushNaturalChannel(ctx).channelInactive();
    }


    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        flushNaturalChannel(ctx).channelWritabilityChanged();
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
        //do noting
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("{}", cause);
    }
}
