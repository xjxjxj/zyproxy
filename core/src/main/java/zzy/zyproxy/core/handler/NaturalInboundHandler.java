package zzy.zyproxy.core.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.util.SharaChannels;

/**
 * @author zhouzhongyuan
 * @date 2016/12/4
 */
public class NaturalInboundHandler extends ChannelInboundHandlerAdapter {

    private final NaturalChannel naturalChannel;
    private final SharaChannels sharaChannels;

    public NaturalInboundHandler(NaturalChannel naturalChannel, SharaChannels sharaChannels) {
        super();
        this.naturalChannel = naturalChannel;
        this.sharaChannels = sharaChannels;
    }

    private NaturalChannel flushNaturalChannel(Channel channel) {
        naturalChannel.flushChannel(channel);
        return naturalChannel;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
