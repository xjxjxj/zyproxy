package zzy.zyproxy.netnat.net.handler;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.handler.NaturalInboundHandler;
import zzy.zyproxy.core.util.SharaChannels;
import zzy.zyproxy.netnat.channel.NatNaturalChannel;
import zzy.zyproxy.netnat.util.NatSharaChannels;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptUserHandler extends NaturalInboundHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptUserHandler.class);

    public AcceptUserHandler(NatNaturalChannel natNaturalChannel, NatSharaChannels natSharaChannels, int port) {
        super(natNaturalChannel,natSharaChannels);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ctx.executor().inEventLoop()
        super.channelRead(ctx, msg);
    }
}
