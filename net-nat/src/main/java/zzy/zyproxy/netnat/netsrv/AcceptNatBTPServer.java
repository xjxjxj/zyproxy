package zzy.zyproxy.netnat.netsrv;

import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsgCodecFactory;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.core.util.ChannelPiplineUtil;
import zzy.zyproxy.netnat.netsrv.handler.AcceptNatBTPInboundHandler;

import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class AcceptNatBTPServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptNatBTPServer.class);
    private final ChannelShare channelShare;
    private final int allIdleTimeSeconds;
    final Timer timer = new HashedWheelTimer();

    public AcceptNatBTPServer(SocketAddress bindAddr, ChannelShare channelShare, int allIdleTimeSeconds) {
        super(bindAddr);
        this.channelShare = channelShare;
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }

    @Override
    protected String getAcceptServerName() {
        return "AcceptNatBTPServer";
    }


    @Override
    protected ChannelPipelineFactory getPipelineFactory() {

        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                HeartMsgCodecFactory.addDecoderAtLast(pipeline);
//                ChannelPiplineUtil.addInfo(pipeline);
                ChannelPiplineUtil.addLast(pipeline,
                    new IdleStateHandler(timer, allIdleTimeSeconds / 2, allIdleTimeSeconds / 2, allIdleTimeSeconds),
                    new AcceptNatBTPInboundHandler(channelShare)
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
