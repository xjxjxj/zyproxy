package zzy.zyproxy.lanserver.netsrv;

import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.codec.msgpackcodec.MsgPackCodecFactory;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.core.server.AcceptServer;

import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class AcceptHeartServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptHeartServer.class);
    private final static Logger HANDLER_LOGGER = LoggerFactory.getLogger(AcceptHeartServerHandler.class);
    private BackChannelPool backChannelPool;

    public AcceptHeartServer(SocketAddress socketAddress) {
        super(socketAddress);
        backChannelPool = new BackChannelPool();
    }

    @Override
    protected String getAcceptServerName() {
        return "AcceptUserServer";
    }

    @Override
    protected ChannelPipelineFactory getPipelineFactory() {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                MsgPackCodecFactory.addDecoderAtLast(pipeline);

                pipeline.addLast(AcceptHeartServerHandler.class.getName(),
                    new AcceptHeartServerHandler());

                MsgPackCodecFactory.addEncoderAtLast(pipeline);
                return pipeline;
            }
        };
    }

    @Override
    protected ChannelFactory getChannelFactory() {
        return new NioServerSocketChannelFactory();
    }

    public class AcceptHeartServerHandler extends SimpleChannelUpstreamHandler {
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            Object message = e.getMessage();
            if (!(message instanceof HeartMsg)) {
                super.messageReceived(ctx, e);
                return;
            }
            //------
            HeartMsg msg0 = (HeartMsg) message;
            Channel channel = ctx.getChannel();
            switch (msg0.getHeartType()) {
                case PING:
                    heartPing(channel, msg0);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            LOGGER.info("exceptionCaught@{}", ctx.getChannel());
            ctx.getChannel().close();
        }

        @Override
        public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            backChannelPool.newCloseBackSrvTask(ctx.getChannel().getId());
        }

        //--------
        private void heartPing(final Channel channel, final HeartMsg heartMsg0) {
            HeartMsg msg = new HeartMsg();
            msg.setHeartType(HeartMsg.HeartType.PONG);
            channel.write(msg).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        backChannelPool.newWatchBackSrvTask(channel);
                    } else {
                        channel.close();
                    }
                }
            });
        }
    }
}
