package zzy.zyproxy.netlan.lansrv;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsgCodecFactory;

import java.net.SocketAddress;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class BackServerClientFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(BackServerClientFactory.class);

    public static void getChannel() {

    }

    SocketAddress socketAddress;

    public BackServerClientFactory(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public void start() {
        ClientBootstrap bootstrap = new ClientBootstrap(
            new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        try {
            bootstrap.setPipelineFactory(getPipelineFactory());
            bootstrap.setOption("tcpNoDelay", true);
            ChannelFuture future = bootstrap.connect(socketAddress);

            future.getChannel().getCloseFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down thread pools to exit.
            bootstrap.shutdown();
        }
    }

    private ChannelPipelineFactory getPipelineFactory() {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                HeartMsgCodecFactory.addDecoderAtLast(pipeline);


                HeartMsgCodecFactory.addEncoderAtLast(pipeline);
                return pipeline;
            }
        };
    }


}
