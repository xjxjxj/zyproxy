package zzy.zyproxy.netnat.net.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.handler.NaturalInboundHandler;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.SharaChannels;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptUserInboundHandler extends NaturalInboundHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptUserInboundHandler.class);

    public AcceptUserInboundHandler(NaturalChannel naturalChannel, SharaChannels sharaChannels) {
        super(naturalChannel, sharaChannels);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ctx.executor().inEventLoop()
        super.channelRead(ctx, msg);
    }
}
