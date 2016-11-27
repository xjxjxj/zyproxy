package zzy.zyproxy.netnat.netsrv;

import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsgCodecFactory;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.core.util.ChannelPiplineUtil;
import zzy.zyproxy.netnat.netsrv.handler.AcceptNatInboundHandler;

import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class AcceptNatServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptNatServer.class);
    private final ChannelShare channelShare;

    public AcceptNatServer(SocketAddress socketAddress, ChannelShare channelShare) {
        super(socketAddress);
        this.channelShare = channelShare;
    }

    @Override
    protected String getAcceptServerName() {
        return "AcceptNatServer";
    }


    @Override
    protected ChannelPipelineFactory getPipelineFactory() {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                HeartMsgCodecFactory.addDecoderAtLast(pipeline);

                ChannelPiplineUtil.addLast(pipeline,
                        new AcceptNatInboundHandler(channelShare)
                );

                HeartMsgCodecFactory.addEncoderAtLast(pipeline);
                return pipeline;
            }
        };
    }

    @Override
    protected ChannelFactory getChannelFactory() {
        return new NioServerSocketChannelFactory();
    }
}
