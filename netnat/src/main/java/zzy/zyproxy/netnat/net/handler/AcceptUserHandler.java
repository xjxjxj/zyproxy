package zzy.zyproxy.netnat.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.NaturalChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptUserHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptUserHandler.class);
    private final NaturalChannel naturalChannel;


    public AcceptUserHandler(NaturalChannel netNaturalChannel) {
        super();
        if (netNaturalChannel == null) {
            throw new NullPointerException("AcceptUserHandler#NaturalChannel");
        }
        this.naturalChannel = netNaturalChannel;
    }

    private NaturalChannel flushNaturalChannel(ChannelHandlerContext ctx) {
        naturalChannel.flushChannelHandlerContext(ctx);
        return naturalChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("handler【1】用户channelActive");
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

        LOGGER.debug("handler【2】用户channelRead");
        flushNaturalChannel(ctx).channelRead(bytes);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("handler【3】用户channelInactive");
        flushNaturalChannel(ctx).channelInactive();
    }


    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("handler【-1】用户channelWritabilityChanged");
        flushNaturalChannel(ctx).channelWritabilityChanged();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("{}", cause);
    }
}
