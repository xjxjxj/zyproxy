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
public abstract class ChannelClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChannelClient.class);
    EventLoopGroup group;

    protected Bootstrap bootstrap() throws InterruptedException {
        return new Bootstrap().group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(handler());
    }


    protected void shutdown() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    public abstract void start();

    protected abstract ChannelInitializer<SocketChannel> handler();
}
