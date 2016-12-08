package zzy.zyproxy.netnat.nat.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.util.ChannelUtil;
import zzy.zyproxy.netnat.nat.channel.NatBTPChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/7
 */
public class RealHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(RealHandler.class);
    private volatile NaturalChannel naturalChannel;

    public RealHandler(NatBTPChannel natBTPChannel) {
        naturalChannel = natBTPChannel.pollUser();
    }


    private NaturalChannel flushNaturalChannel(ChannelHandlerContext ctx) {
        if (naturalChannel == null) {
            ChannelUtil.flushAndClose(ctx);
            return null;
        }
        naturalChannel.flushChannelHandlerContext(ctx);
        return naturalChannel;
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
        LOGGER.debug("channelRead:{}", ctx);
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
        LOGGER.warn("{}", cause);
    }

    protected void active(final NaturalChannel naturalChannel) {
        naturalChannel.writeToBTPChannelConnected().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    naturalChannel.ctxRead();
                } else {
                    naturalChannel.flushAndClose();
                }
            }
        });
    }

    protected void read(final NaturalChannel naturalChannel, byte[] msg) {
        naturalChannel.writeToBTPChannelTransmit(msg).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    naturalChannel.ctxRead();
                } else {
                    naturalChannel.flushAndClose();
                }
            }
        });
    }

    protected void inActive(NaturalChannel naturalChannel) {
        naturalChannel.writeToBTPChannelClose();
    }

    protected void writabilityChanged(NaturalChannel naturalChannel) {

    }
}
