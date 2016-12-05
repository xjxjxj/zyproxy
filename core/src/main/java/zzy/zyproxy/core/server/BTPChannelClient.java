package zzy.zyproxy.core.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public abstract class BTPChannelClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(BTPChannelClient.class);

    protected ChannelFuture bootstrap(InetSocketAddress connectAddr) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap().group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(handler());

        return bootstrap.connect(connectAddr).sync();
    }

    protected abstract ChannelInitializer<SocketChannel> handler();

    public abstract void start();
}
