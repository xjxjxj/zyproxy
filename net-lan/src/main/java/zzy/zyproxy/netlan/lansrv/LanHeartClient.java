package zzy.zyproxy.netlan.lansrv;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsgCodecFactory;
import zzy.zyproxy.core.util.ChannelPiplineUtil;
import zzy.zyproxy.netlan.lansrv.handler.LanHeartInboundHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class LanHeartClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(LanHeartClient.class);

    private final int allIdleTimeSeconds;
    private final InetSocketAddress acptHeartAddr;
    private final InetSocketAddress acptUserAddr;

    private BackClientFactory backClientFactory;

    public LanHeartClient(InetSocketAddress acptHeartAddr, InetSocketAddress acptUserAddr,
                          InetSocketAddress acptBackAddr, InetSocketAddress lanRealAddr,
                          int allIdleTimeSeconds) {
        this.acptHeartAddr = acptHeartAddr;
        this.acptUserAddr = acptUserAddr;
        this.allIdleTimeSeconds = allIdleTimeSeconds;

        RealClientFactory realClientFactory = new RealClientFactory(lanRealAddr);
        this.backClientFactory = new BackClientFactory(acptBackAddr, realClientFactory);
    }


    public void start() {
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        try {
            bootstrap.setPipelineFactory(getPipelineFactory());
            bootstrap.setOption("tcpNoDelay", true);
            ChannelFuture future = bootstrap.connect(acptHeartAddr);

            future.getChannel().getCloseFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down thread pools to exit.
            bootstrap.shutdown();
        }
    }

    private ChannelPipelineFactory getPipelineFactory() {
        final Timer timer = new HashedWheelTimer();

        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                HeartMsgCodecFactory.addDecoderAtLast(pipeline);

                ChannelPiplineUtil.addLast(pipeline,
                        new IdleStateHandler(timer, 10, 10, allIdleTimeSeconds),
                        new LanHeartInboundHandler(
                                LanHeartClient.this,
                                backClientFactory,
                                acptUserAddr
                        ));

                HeartMsgCodecFactory.addEncoderAtLast(pipeline);
                return pipeline;
            }
        };
    }

    public void reConnect() {

    }
}
