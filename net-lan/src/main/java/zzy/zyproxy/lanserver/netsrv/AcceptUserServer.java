package zzy.zyproxy.lanserver.netsrv;

import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.codec.msgpackcodec.MsgPackCodecFactory;
import zzy.zyproxy.core.server.AcceptServer;

import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class AcceptUserServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptUserServer.class);
    private final static Logger HANDLER_LOGGER = LoggerFactory.getLogger(AcceptUserServerHandler.class);

    public AcceptUserServer(SocketAddress socketAddress) {
        super(socketAddress);
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

                pipeline.addLast(AcceptUserServerHandler.class.getName(),
                    new AcceptUserServerHandler());

                MsgPackCodecFactory.addEncoderAtLast(pipeline);
                return pipeline;
            }
        };
    }

    @Override
    protected ChannelFactory getChannelFactory() {
        return new NioServerSocketChannelFactory();
    }

    public class AcceptUserServerHandler extends SimpleChannelUpstreamHandler {

        @Override
        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            // Suspend incoming traffic until connected to the remote host.
            final Channel inboundChannel = e.getChannel();
            inboundChannel.setReadable(false);


        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
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
            LOGGER.debug("exceptionCaught");
        }

        @Override
        public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
            LOGGER.debug("writeComplete");
        }
    }
}
