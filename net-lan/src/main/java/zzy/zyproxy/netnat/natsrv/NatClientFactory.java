package zzy.zyproxy.netnat.natsrv;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsgCodecFactory;
import zzy.zyproxy.core.util.ChannelPiplineUtil;
import zzy.zyproxy.netnat.natsrv.handler.NatInboundHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class NatClientFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatClientFactory.class);
    private final InetSocketAddress acptBackAddr;
    private final InetSocketAddress acptUserAddr;
    private final RealClientFactory realClientFactory;

    ExecutorService bossExecutor = Executors.newCachedThreadPool();
    ExecutorService workExecutor = Executors.newCachedThreadPool();



    NatClientFactory(InetSocketAddress acptBackAddr, InetSocketAddress acptUserAddr, RealClientFactory realClientFactory) {
        this.acptBackAddr = acptBackAddr;
        this.acptUserAddr = acptUserAddr;
        this.realClientFactory = realClientFactory;
    }


    private ChannelPipelineFactory getPipelineFactory() {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                HeartMsgCodecFactory.addDecoderAtLast(pipeline);

                ChannelPiplineUtil.addLast(pipeline,
                        new NatInboundHandler(realClientFactory,acptUserAddr));

                HeartMsgCodecFactory.addEncoderAtLast(pipeline);
                return pipeline;
            }
        };
    }


    public ChannelFuture getBackClient(int netRequestNewChannelNum) {
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        bossExecutor,
                        workExecutor));
        bootstrap.setPipelineFactory(getPipelineFactory());
        bootstrap.setOption("tcpNoDelay", true);
        return bootstrap.connect(acptBackAddr);
    }
}
