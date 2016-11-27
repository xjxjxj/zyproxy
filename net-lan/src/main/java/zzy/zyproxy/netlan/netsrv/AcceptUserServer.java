package zzy.zyproxy.netlan.netsrv;

import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.heart.HeartMsgCodecFactory;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.core.util.ChannelPiplineUtil;
import zzy.zyproxy.netlan.netsrv.handler.AcceptUserInboundHandler;

import java.net.SocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public final class AcceptUserServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptUserServer.class);
    private final ChannelShare channelShare;

    public AcceptUserServer(SocketAddress socketAddress, ChannelShare channelShare) {
        super(socketAddress);
        this.channelShare = channelShare;
    }

    @Override
    protected String getAcceptServerName() {
        return "AcceptUserServer";
    }

    @Override
    protected ChannelPipelineFactory getPipelineFactory() {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                HeartMsgCodecFactory.addDecoderAtLast(pipeline);

                ChannelPiplineUtil.addLast(pipeline,
                        new AcceptUserInboundHandler(channelShare));

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
