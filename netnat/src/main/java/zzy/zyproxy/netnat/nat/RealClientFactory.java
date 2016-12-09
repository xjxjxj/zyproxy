package zzy.zyproxy.netnat.nat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.netnat.nat.channel.NatBTPChannel;
import zzy.zyproxy.netnat.nat.channel.NatNaturalChannel;
import zzy.zyproxy.netnat.nat.handler.RealHandler;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/7
 */
public class RealClientFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(RealClientFactory.class);
    private final InetSocketAddress realAddr;
    private final NatBTPChannel natBTPChannel;

    public RealClientFactory(NatBTPChannel natBTPChannel, InetSocketAddress realAddr) {
        if (natBTPChannel == null) {
            throw new NullPointerException("RealClientFactory#NatBTPChannel");
        }
        this.natBTPChannel = natBTPChannel;
        this.realAddr = realAddr;
    }

    private Bootstrap bootstrap;
    EventLoopGroup group = new NioEventLoopGroup();

    public Bootstrap bootstrap() {
        if (bootstrap == null) {
            this.bootstrap
                = new Bootstrap().group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.AUTO_READ, false)
                .handler(new Initializer());
        }
        return bootstrap;
    }

    public ChannelFuture createClient(InetSocketAddress address) {
        return bootstrap().connect(address);
    }

    public ChannelFuture createClient() {
        if (realAddr == null) {
            throw new NullPointerException("RealClientFactory#createClient#realAddr");
        }
        return createClient(realAddr);
    }

    class Initializer extends ChannelInitializer<SocketChannel> {

        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new RealHandler(new NatNaturalChannel(natBTPChannel)));
        }
    }
}
