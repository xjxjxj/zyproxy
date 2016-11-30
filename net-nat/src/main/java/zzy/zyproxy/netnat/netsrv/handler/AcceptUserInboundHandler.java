package zzy.zyproxy.netnat.netsrv.handler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.ProxyChannel;
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

    private UserNatBTPChannel.UserChannel flushChannel(Channel channel) {
        return userChannel == null ? null : userChannel.flushChannel(channel);
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
                        ProxyChannel.closea(channel);

                        return;
                    }
                    SocketAddress localAddress = channel.getLocalAddress();
                    if (!(localAddress instanceof InetSocketAddress)) {
                        ProxyChannel.closea(channel);

                        return;
                    }
                    InetSocketAddress localAdd = (InetSocketAddress) localAddress;
                    channelShare.takeUserToNatChannel(channel, localAdd,
                        new ChannelShare.takeUserToNatChannelCallable() {
                            public void call(UserNatBTPChannel.UserChannel userChannel0) {
                                if (userChannel0 == null) {
                                    ProxyChannel.closea(channel);
                                    return;
                                }
                                userChannel = userChannel0;
                                flushChannel(channel);
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
                        ProxyChannel.closea(channel);

                        return;
                    }
                    LOGGER.debug("UserChannelwriteChannelConnected#runable");
                    flushChannel(channel).writeConnected(new Runnable() {
                        public void run() {
                            try {
                                LOGGER.debug("UserChannelwriteChannelConnected#runable#writeConnected call back and setReadable true");
                                channel.setReadable(true);
                            } catch (Exception e) {
                                ProxyChannel.closea(channel);

                            }
                        }
                    });
                } catch (Exception e) {
                    ProxyChannel.closea(channel);

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
        flushChannel(channel)
            .writeToNatBTP(bytes)
            .addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        LOGGER.debug("从用户端read数据【成功】写到NatBTP中  channel.setReadable(true)");
                        channel.setReadable(true);
                    } else {
                        ProxyChannel.closea(channel);

                    }
                }
            });
    }


    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        UserNatBTPChannel.UserChannel userChannel = flushChannel(ctx.getChannel());
        if (userChannel != null) {
            userChannel.writeUserChannelClosed();
        }
    }


    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Channel channel = ctx.getChannel();
        LOGGER.warn("exceptionCaught:{}:{}:{}:{}:{}--{}", channel.isBound(), channel.isWritable(), channel.isConnected(), channel.isOpen(), channel.isReadable(), System.currentTimeMillis());
    }
}
