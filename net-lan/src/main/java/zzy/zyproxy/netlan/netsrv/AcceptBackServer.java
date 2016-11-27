package zzy.zyproxy.netlan.netsrv;

import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.server.AcceptServer;

import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class AcceptBackServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptBackServer.class);
    private final static Logger HANDLER_LOGGER = LoggerFactory.getLogger(AcceptBackServerHandler.class);

    public AcceptBackServer(SocketAddress socketAddress, ChannelShare channelShare) {
        super(socketAddress);
    }

    @Override
    protected String getAcceptServerName() {
        return "AcceptBackServer";
    }


    @Override
    protected ChannelPipelineFactory getPipelineFactory() {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();

                pipeline.addLast(
                    AcceptBackServerHandler.class.getName(),
                    new AcceptBackServerHandler()
                );

                return pipeline;
            }
        };
    }

    @Override
    protected ChannelFactory getChannelFactory() {
        return new NioServerSocketChannelFactory();
    }

    public class AcceptBackServerHandler extends SimpleChannelUpstreamHandler {

        @Override
        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            super.channelConnected(ctx, e);
        }

        @Override
        public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            super.channelDisconnected(ctx, e);
        }

        @Override
        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            super.channelOpen(ctx, e);
        }

        @Override
        public void channelUnbound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            super.channelUnbound(ctx, e);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            super.exceptionCaught(ctx, e);
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            super.messageReceived(ctx, e);
        }

        @Override
        public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
            super.writeComplete(ctx, e);
        }
    }
}
