package zzy.zyproxy.netnat.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.util.ChannelUtil;
import zzy.zyproxy.netnat.channel.NetNatNaturalChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptUserHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptUserHandler.class);
    private final BTPChannel btpChannel;
    private NaturalChannel netNatNaturalChannel;

    public AcceptUserHandler(BTPChannel btpChannel) {
        super();
        this.btpChannel = btpChannel;
    }

    private NaturalChannel flushNaturalChannel(ChannelHandlerContext ctx) {
        if (netNatNaturalChannel == null) {
            ChannelUtil.flushAndClose(ctx);
            return null;
        }
        netNatNaturalChannel.flushChannelHandlerContext(ctx);
        return netNatNaturalChannel;
    }

    private NetNatNaturalChannel newNetNatNaturalChannel(ChannelHandlerContext ctx) {
        int userCode = ctx.hashCode();
        NetNatNaturalChannel netNatNaturalChannel = new NetNatNaturalChannel(ctx, userCode, btpChannel);
        btpChannel.putNaturalChannel(userCode, netNatNaturalChannel);
        return netNatNaturalChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("channelActive:{}", ctx);
        netNatNaturalChannel = newNetNatNaturalChannel(ctx);
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
        //event do nothing
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("{}", this.getClass().getSimpleName(), cause);
    }

    //===========
    protected void active(final NaturalChannel naturalChannel) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    naturalChannel.ctxRead();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        naturalChannel.regConnectedEvent(runnable);
        naturalChannel
            .writeToBTPChannelConnected()
            .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    protected void read(final NaturalChannel naturalChannel, byte[] msg) {
        naturalChannel
            .writeToBTPChannelTransmit(msg)
            .addListener(new ChannelFutureListener() {
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
