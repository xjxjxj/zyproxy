package zzy.zyproxy.netnat.netsrv;

import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsgCodecFactory;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.core.util.ChannelPiplineUtil;
import zzy.zyproxy.netnat.netsrv.handler.AcceptHeartInboundHandler;

import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class AcceptHeartServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptHeartServer.class);

    private ChannelShare channelShare;
    private int allIdleTimeSeconds;

    public AcceptHeartServer(SocketAddress socketAddress, ChannelShare channelShare, int allIdleTimeSeconds) {
        super(socketAddress);
        this.channelShare = channelShare;
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }

    @Override
    protected String getAcceptServerName() {
        return "AcceptHeartServer";
    }

    @Override
    protected ChannelPipelineFactory getPipelineFactory() {
        final Timer timer = new HashedWheelTimer();
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();

                HeartMsgCodecFactory.addDecoderAtLast(pipeline);

                ChannelPiplineUtil.addLast(pipeline,
                        new IdleStateHandler(timer, allIdleTimeSeconds / 2, allIdleTimeSeconds / 2, allIdleTimeSeconds),
                        new AcceptHeartInboundHandler(channelShare)
                );

                HeartMsgCodecFactory.addEncoderAtLast(pipeline);
                return pipeline;
            }
        };
    }

    @Override
    protected ChannelFactory getChannelFactory() {
        return new NioServerSocketChannelFactory();
    }
}
