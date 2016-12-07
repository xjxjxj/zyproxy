package zzy.zyproxy.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.NaturalChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public abstract class NaturalInboundHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(NaturalInboundHandler.class);

    private NaturalChannel naturalChannel;

    public NaturalInboundHandler(NaturalChannel naturalChannel) {
        super();
        this.naturalChannel = naturalChannel;
    }

    public void setNaturalChannel(NaturalChannel naturalChannel) {
        this.naturalChannel = naturalChannel;
    }

    private NaturalChannel flushNaturalChannel(ChannelHandlerContext ctx) {
        naturalChannel.flushChannelHandlerContext(ctx);
        return naturalChannel;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("channelRegistered:{}", ctx);
        ctx.read();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("channelActive:{}", ctx);
        NaturalChannel naturalChannel = flushNaturalChannel(ctx);
        active(naturalChannel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            return;
        }
        NaturalChannel naturalChannel = flushNaturalChannel(ctx);
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        LOGGER.debug("channelRead:{};msg:{}", ctx, bytes);

        read(naturalChannel, bytes);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NaturalChannel naturalChannel = flushNaturalChannel(ctx);
        LOGGER.debug("channelInactive:{}", ctx);

        inActive(naturalChannel);
    }


    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        NaturalChannel naturalChannel = flushNaturalChannel(ctx);
        LOGGER.debug("channelWritabilityChanged:{}", ctx);
        writabilityChanged(naturalChannel);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("channelReadComplete:{}", ctx);
        ctx.read();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("", cause);
    }

    protected abstract void active(NaturalChannel naturalChannel);

    protected abstract void read(NaturalChannel naturalChannel, byte[] msg);

    protected abstract void inActive(NaturalChannel naturalChannel);

    protected abstract void writabilityChanged(NaturalChannel naturalChannel);
}
