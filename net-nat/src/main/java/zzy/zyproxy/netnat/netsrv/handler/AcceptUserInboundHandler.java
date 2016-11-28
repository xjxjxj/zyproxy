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
    private volatile UserNatBTPChannel userNatBTPChannel;
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
                    channelShare.takeUserToNatChannel(localAdd,
                        new ChannelShare.takeUserToNatChannelCallable() {
                            public void call(UserNatBTPChannel userNatBTPChannel0) {
                                if (userNatBTPChannel0 == null) {
                                    channel.close();
                                    return;
                                }
                                userNatBTPChannel = userNatBTPChannel0;
                                userNatBTPChannel.flushUserChannel(channel);
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
                    if (userNatBTPChannel == null) {
                        channel.close();
                        return;
                    }
                    UserNatBTPChannel.UserChannel userChannel
                        = userNatBTPChannel.flushUserChannel(channel);
                    LOGGER.debug("UserChannelwriteChannelConnected#runable");
                    userChannel.writeChannelConnected(new Runnable() {
                        public void run() {
                            try {
                                LOGGER.debug("UserChannelwriteChannelConnected#runable#writeChannelConnected call back and setReadable true");
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
        if (userNatBTPChannel != null) {
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
        userNatBTPChannel
            .flushUserChannel(channel)
            .writeToNatBTP(bytes)
            .addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        LOGGER.debug("从用户端read数据【成功】写到NatBTP中  channel.setReadable(true)");
                        channel.setReadable(true);
                    } else {
                        userNatBTPChannel.close();
                    }
                }
            });
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LOGGER.debug("writeUserChannelClosed");
        userNatBTPChannel.flushUserChannel(ctx.getChannel()).writeUserChannelClosed();
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
