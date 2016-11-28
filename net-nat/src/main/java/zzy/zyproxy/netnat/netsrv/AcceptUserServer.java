package zzy.zyproxy.netnat.netsrv;

import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.core.util.ChannelPiplineUtil;
import zzy.zyproxy.netnat.netsrv.handler.AcceptUserInboundHandler;

import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class AcceptUserServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptUserServer.class);
    private final ChannelShare channelShare;

    public AcceptUserServer(SocketAddress socketAddress, ChannelShare channelShare) {
        super(socketAddress);
        this.channelShare = channelShare;
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
                ChannelPiplineUtil.addLast(pipeline,
                    new LoggingHandler(InternalLogLevel.INFO),
                    new AcceptUserInboundHandler(channelShare));
                return pipeline;
            }
        };
    }

    @Override
    protected ChannelFactory getChannelFactory() {
        return new NioServerSocketChannelFactory();
    }


}
