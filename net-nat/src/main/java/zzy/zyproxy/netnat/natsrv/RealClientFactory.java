package zzy.zyproxy.netnat.natsrv;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.util.ChannelPiplineUtil;
import zzy.zyproxy.netnat.natsrv.channel.RealNatBTPChannel;
import zzy.zyproxy.netnat.natsrv.handler.RealInboundHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class RealClientFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(RealClientFactory.class);
    private ClientBootstrap bootstrap;

    AtomicInteger integer = new AtomicInteger();
    BossPool<NioClientBoss> bossPool = new NioClientBossPool(Executors.newCachedThreadPool(), 1);
    WorkerPool<NioWorker> workerPool = new NioWorkerPool(Executors.newCachedThreadPool(), Runtime.getRuntime().availableProcessors() * 2);

    InetSocketAddress natRealAddr;
    private ChannelPipelineFactory channelPipelineFactory;


    RealClientFactory(InetSocketAddress natRealAddr) {
        this.natRealAddr = natRealAddr;
        this.bootstrap = new ClientBootstrap(
            new NioClientSocketChannelFactory(
                bossPool,
                workerPool));
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.reuseAddress", true);
        this.channelPipelineFactory = new PiplineFactory();
    }


    public ClientBootstrap getBootstrap() {
        int i = integer.getAndIncrement();
        System.out.println(i);
        return bootstrap;
    }

    public void createRealClient(RealNatBTPChannel.RealChannel realChannel) {
        ClientBootstrap bootstrap = getBootstrap();
        bootstrap.setPipelineFactory(channelPipelineFactory);
        Channel channel = bootstrap.connect(natRealAddr).getChannel();
        RealInboundHandler realInboundHandler = channel.getPipeline().get(RealInboundHandler.class);
        realChannel.flushChannel(channel);
        realInboundHandler.setRealChannel(realChannel);
    }

    class PiplineFactory implements ChannelPipelineFactory {
        public ChannelPipeline getPipeline() throws Exception {
            ChannelPipeline pipeline = Channels.pipeline();
            ChannelPiplineUtil.addLast(pipeline,
                new RealInboundHandler());
            return pipeline;
        }
    }
}
