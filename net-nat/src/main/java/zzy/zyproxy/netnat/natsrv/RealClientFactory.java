package zzy.zyproxy.netnat.natsrv;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsgCodecFactory;
import zzy.zyproxy.core.util.ChannelPiplineUtil;
import zzy.zyproxy.netnat.natsrv.channel.RealNatBTPChannel;
import zzy.zyproxy.netnat.natsrv.handler.RealInboundHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class RealClientFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(RealClientFactory.class);

    ExecutorService bossExecutor = Executors.newCachedThreadPool();
    ExecutorService workExecutor = Executors.newCachedThreadPool();

    InetSocketAddress natRealAddr;

    RealClientFactory(InetSocketAddress natRealAddr) {
        this.natRealAddr = natRealAddr;
    }

    private ChannelPipelineFactory getPipelineFactory(final RealNatBTPChannel realNatBTPChannel) {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                ChannelPiplineUtil.addLast(pipeline,
                    new RealInboundHandler(realNatBTPChannel));
                return pipeline;
            }
        };
    }

    public ChannelFuture getRealClient(RealNatBTPChannel realNatBTPChannel) {
        ClientBootstrap bootstrap = new ClientBootstrap(
            new NioClientSocketChannelFactory(
                bossExecutor,
                workExecutor));
        bootstrap.setPipelineFactory(getPipelineFactory(realNatBTPChannel));
        bootstrap.setOption("tcpNoDelay", true);
        return bootstrap.connect(natRealAddr);
    }
}
