package zzy.zyproxy.netlan.lansrv;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsgCodecFactory;
import zzy.zyproxy.core.util.ChannelPiplineUtil;
import zzy.zyproxy.netlan.lansrv.handler.BackInboundHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class BackClientFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(BackClientFactory.class);

    ExecutorService bossExecutor = Executors.newCachedThreadPool();
    ExecutorService workExecutor = Executors.newCachedThreadPool();


    InetSocketAddress acptBackAddr;
    private final RealClientFactory realClientFactory;
    private Object backClient;

    BackClientFactory(InetSocketAddress acptBackAddr, RealClientFactory realClientFactory) {
        this.acptBackAddr = acptBackAddr;
        this.realClientFactory = realClientFactory;
    }

    private ChannelPipelineFactory getPipelineFactory() {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                HeartMsgCodecFactory.addDecoderAtLast(pipeline);
                ChannelPiplineUtil.addLast(pipeline,
                        new BackInboundHandler(realClientFactory));
                HeartMsgCodecFactory.addEncoderAtLast(pipeline);
                return pipeline;
            }
        };
    }


    public ChannelFuture getBackClient() {
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        bossExecutor,
                        workExecutor));
        bootstrap.setPipelineFactory(getPipelineFactory());
        bootstrap.setOption("tcpNoDelay", true);
        return bootstrap.connect(acptBackAddr);
    }
}
