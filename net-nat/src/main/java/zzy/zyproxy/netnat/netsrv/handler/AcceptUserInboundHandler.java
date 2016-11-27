package zzy.zyproxy.netnat.netsrv.handler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.netnat.netsrv.ChannelShare;
import zzy.zyproxy.netnat.netsrv.channel.UserNatBTPChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/26
 */
public class AcceptUserInboundHandler extends SimpleChannelUpstreamHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptUserInboundHandler.class);
    private final ChannelShare channelShare;
    private UserNatBTPChannel userNatBTPChannel;

    public AcceptUserInboundHandler(ChannelShare channelShare) {
        this.channelShare = channelShare;
    }

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        final Channel channel = e.getChannel();
        channel.setReadable(false).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    LOGGER.debug("channel.setReadable(false)#isSuccess");
                    channel.close();
                }
                SocketAddress localAddress = channel.getLocalAddress();
                if (localAddress instanceof InetSocketAddress) {
                    InetSocketAddress localAdd = (InetSocketAddress) localAddress;
                    channelShare.takeUserToNatChannel(channel, localAdd, new ChannelShare.takeUserToNatChannelCallable() {
                        public void call(UserNatBTPChannel userNatBTPChannel0) {
                            channel.setReadable(true);
                            if (userNatBTPChannel0 == null) {
                                channel.close();
                            }
                            userNatBTPChannel = userNatBTPChannel0;
                        }
                    });
                }
            }
        });
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
        userNatBTPChannel.userWriteToNatBTP(bytes).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    channel.setReadable(true);
                } else {
                    channel.close();
                }
            }
        });
        LOGGER.debug("messageReceived");
    }

    @Override
    public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOGGER.debug("channelBound");
    }

    @Override
    public void channelUnbound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOGGER.debug("channelUnbound");
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOGGER.debug("channelClosed");
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOGGER.debug("channelConnected");
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOGGER.debug("channelDisconnected");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LOGGER.debug("exceptionCaught，e:{}", e.getCause());
    }

    @Override
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
        LOGGER.debug("writeComplete");
    }
}
