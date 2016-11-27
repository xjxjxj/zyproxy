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
import java.net.SocketAddress;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class LanHeartServerClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(LanHeartServerClient.class);

    private final SocketAddress netHeartAddress;
    private final InetSocketAddress lanProxyAddr;
    private final int allIdleTimeSeconds;

    public LanHeartServerClient(SocketAddress netHeartAddress, InetSocketAddress lanProxyAddr, int allIdleTimeSeconds) {
        this.netHeartAddress = netHeartAddress;
        this.lanProxyAddr = lanProxyAddr;
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }

    public void start() {
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        try {
            bootstrap.setPipelineFactory(getPipelineFactory());
            bootstrap.setOption("tcpNoDelay", true);
            ChannelFuture future = bootstrap.connect(netHeartAddress);

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
                        new LanHeartInboundHandler(LanHeartServerClient.this, lanProxyAddr));

                HeartMsgCodecFactory.addEncoderAtLast(pipeline);
                return pipeline;
            }
        };
    }

    public void reConnect() {

    }
}
