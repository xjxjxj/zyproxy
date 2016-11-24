package zzy.zyproxy.lanserver.server;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.codec.msgpackcodec.MsgPackCodecFactory;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.lanserver.handler.AcceptBackServerHandle;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public class AcceptBackServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptBackServer.class);

    @Override
    protected SocketAddress getInetSocketAddress() {
        return new InetSocketAddress(6009);
    }

    @Override
    protected ChannelPipelineFactory getPipelineFactory() {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                MsgPackCodecFactory.addDecoderAtLast(pipeline);

                pipeline.addLast(AcceptBackServerHandle.class.getName(),
                    new AcceptBackServerHandle());

                MsgPackCodecFactory.addEncoderAtLast(pipeline);
                return pipeline;
            }
        };
    }

    @Override
    protected ChannelFactory getChannelFactory() {
        return new NioServerSocketChannelFactory();
    }
}
