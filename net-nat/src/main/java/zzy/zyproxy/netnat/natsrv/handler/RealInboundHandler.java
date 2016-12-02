package zzy.zyproxy.netnat.natsrv.handler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.util.ChannelUtil;
import zzy.zyproxy.netnat.natsrv.channel.RealNatBTPChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class RealInboundHandler extends SimpleChannelUpstreamHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(RealInboundHandler.class);

    private RealNatBTPChannel.RealChannel realChannel;

    public RealInboundHandler setRealChannel(RealNatBTPChannel.RealChannel realChannel) {
        this.realChannel = realChannel;
        return this;
    }

    private RealNatBTPChannel.RealChannel flushChannel(Channel channel) {
        return realChannel == null ? null : realChannel.flushChannel(channel);
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        final Channel channel = ctx.getChannel();
        flushChannel(channel);
        channel.setReadable(false);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        final Channel channel = ctx.getChannel();
        flushChannel(channel)
            .writeRealConnected()
            .addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        ChannelUtil.closeOnFlush(channel);
                        return;
                    }
                    LOGGER.debug("writeConnected & setReadable true");
                    channel.setReadable(true);
                }
            });
        LOGGER.debug("channelOpen & setReadable false,remote address:{}", channel.getRemoteAddress());
    }


    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        final Channel channel = ctx.getChannel();
        if (!(message instanceof ChannelBuffer)) {
            super.messageReceived(ctx, e);
            return;
        }
        ChannelBuffer buffer = (ChannelBuffer) message;
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        channel.setReadable(false);
        LOGGER.debug("真实服务器client接受到信息，【开始】发送给BTP，并且setReadable false");
        flushChannel(channel)
            .writeToNatBTP(bytes)
            .addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        channel.setReadable(true);
                        LOGGER.debug("真实服务器client接受到信息，发送给BTP【成功】，并且setReadable true");
                    } else {
                        ChannelUtil.closeOnFlush(channel);
                    }
                }
            });
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        RealNatBTPChannel.RealChannel realChannel = flushChannel(ctx.getChannel());
        if (realChannel != null) {
            realChannel
                .writeRealChannelClosed();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Channel channel = ctx.getChannel();
        LOGGER.error("channelClosed Message:{} |isBound: {} |isWritable:{} |isConnected:{} |isOpen:{} |isReadable:{} |时间--{}",
            e,
            channel.isBound(),
            channel.isWritable(), channel.isConnected(), channel.isOpen(), channel.isReadable(), System.currentTimeMillis());
    }
}
