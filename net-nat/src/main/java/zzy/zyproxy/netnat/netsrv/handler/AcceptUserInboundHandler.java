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
    private volatile UserNatBTPChannel.UserChannel userChannel;
    private volatile Runnable channelConnectedTask;

    public AcceptUserInboundHandler(ChannelShare channelShare) {
        this.channelShare = channelShare;
    }

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        final Channel channel = e.getChannel();
        channel
            .setReadable(false)
            .addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        LOGGER.debug("channel.setReadable(false)#isSuccess");
                        channel.close();
                        return;
                    }
                    SocketAddress localAddress = channel.getLocalAddress();
                    if (!(localAddress instanceof InetSocketAddress)) {
                        channel.close();
                        return;
                    }
                    InetSocketAddress localAdd = (InetSocketAddress) localAddress;
                    channelShare.takeUserToNatChannel(channel,localAdd,
                        new ChannelShare.takeUserToNatChannelCallable() {
                            public void call(UserNatBTPChannel.UserChannel userChannel0) {
                                if (userChannel0 == null) {
                                    channel.close();
                                    return;
                                }
                                userChannel = userChannel0;
                                userChannel.flushChannel(channel);
                                if (channelConnectedTask != null) {
                                    channelConnectedTask.run();
                                }
                            }
                        }
                    );
                }
            });
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        final Channel channel = ctx.getChannel();
        final Runnable runnable = new Runnable() {
            public void run() {
                try {
                    if (userChannel == null) {
                        channel.close();
                        return;
                    }
                    userChannel.flushChannel(channel);
                    LOGGER.debug("UserChannelwriteChannelConnected#runable");
                    userChannel.writeConnected(new Runnable() {
                        public void run() {
                            try {
                                LOGGER.debug("UserChannelwriteChannelConnected#runable#writeConnected call back and setReadable true");
                                channel.setReadable(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        if (userChannel != null) {
            runnable.run();
        } else {
            channelConnectedTask = runnable;
        }
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
        LOGGER.debug("从用户端read数据【开始】写到NatBTP中 channel.setReadable(false)");
        userChannel
            .flushChannel(channel)
            .writeToNatBTP(bytes)
            .addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        LOGGER.debug("从用户端read数据【成功】写到NatBTP中  channel.setReadable(true)");
                        channel.setReadable(true);
                    } else {
                        userChannel.close();
                    }
                }
            });
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOGGER.debug("writeUserChannelClosed");
        userChannel.flushChannel(ctx.getChannel()).writeUserChannelClosed();
        Channel channel = ctx.getChannel();
        LOGGER.warn("channelClosed:{}:{}:{}:{}:{}--{}", channel.isBound(), channel.isWritable(), channel.isConnected(), channel.isOpen(), channel.isReadable(), System.currentTimeMillis());
    }


    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Channel channel = ctx.getChannel();
        LOGGER.warn("exceptionCaught:{}:{}:{}:{}:{}--{}",channel.isBound(),channel.isWritable(),channel.isConnected(),channel.isOpen(),channel.isReadable(),System.currentTimeMillis());
    }

    @Override
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
        LOGGER.debug("writeComplete");
    }
}
