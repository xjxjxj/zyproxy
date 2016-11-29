package zzy.zyproxy.netnat.natsrv;

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
import zzy.zyproxy.netnat.natsrv.handler.NatBTPInboundHandler;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class NatBTPClientServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPClientServer.class);

    private final RealClientFactory realClientFactory;
    private final NatBTPClientFactory natBTPClientFactory;
    private final ArrayList<Channel> channels = new ArrayList<Channel>();

    public NatBTPClientServer(InetSocketAddress acptUserAddr,
                              InetSocketAddress acptBTPAddr, InetSocketAddress natRealAddr,
                              int allIdleTimeSeconds) {
        this.realClientFactory = new RealClientFactory(natRealAddr);
        this.natBTPClientFactory = new NatBTPClientFactory(acptBTPAddr, acptUserAddr, allIdleTimeSeconds);
    }


    public void start(int initNum) {
        for (int i = 0; i < initNum; i++) {
            ChannelFuture btpClient = natBTPClientFactory.getBTPClient();
            channels.add(btpClient.getChannel());
        }
    }


    public void reConnect() {

    }

    public final class NatBTPClientFactory {
        private final Logger LOGGER = LoggerFactory.getLogger(NatBTPClientFactory.class);
        private final InetSocketAddress acptBTPAddr;
        private final InetSocketAddress acptUserAddr;
        private final int allIdleTimeSeconds;

        public NatBTPClientFactory(InetSocketAddress acptBTPAddr, InetSocketAddress acptUserAddr, int allIdleTimeSeconds) {
            this.acptBTPAddr = acptBTPAddr;
            this.acptUserAddr = acptUserAddr;
            this.allIdleTimeSeconds = allIdleTimeSeconds;
        }

        private ChannelPipelineFactory getPipelineFactory() {
            return new ChannelPipelineFactory() {
                public ChannelPipeline getPipeline() throws Exception {
                    ChannelPipeline pipeline = Channels.pipeline();
                    HeartMsgCodecFactory.addDecoderAtLast(pipeline);

                    ChannelPiplineUtil.addLast(pipeline,
                        new IdleStateHandler(new HashedWheelTimer(), 10, 10, allIdleTimeSeconds),
                        new NatBTPInboundHandler(realClientFactory, acptUserAddr));

                    HeartMsgCodecFactory.addEncoderAtLast(pipeline);
                    return pipeline;
                }
            };
        }


        public ChannelFuture getBTPClient() {
            ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory());
            bootstrap.setPipelineFactory(getPipelineFactory());
            bootstrap.setOption("tcpNoDelay", true);
            return bootstrap.connect(acptBTPAddr);
        }
    }
}
