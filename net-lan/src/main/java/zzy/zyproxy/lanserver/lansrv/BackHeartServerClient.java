package zzy.zyproxy.lanserver.lansrv;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.codec.msgpackcodec.MsgPackCodecFactory;

import java.net.SocketAddress;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class BackHeartServerClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(BackHeartServerClient.class);

    SocketAddress socketAddress;

    public BackHeartServerClient(SocketAddress socketAddress) {
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

    protected ChannelPipelineFactory getPipelineFactory() {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                MsgPackCodecFactory.addDecoderAtLast(pipeline);

                pipeline.addLast(BackHeartServerHandler.class.getName(),
                    new BackHeartServerHandler());

                MsgPackCodecFactory.addEncoderAtLast(pipeline);
                return pipeline;
            }
        };
    }


    private class BackHeartServerHandler extends SimpleChannelUpstreamHandler {

    }
}
