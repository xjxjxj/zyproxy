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
public class ServerClient {
    private final EventLoopGroup group;
    private final ChannelInitializer<SocketChannel> handler;

    public ServerClient(EventLoopGroup group, ChannelInitializer<SocketChannel> handler) {
        this.group = group;
        this.handler = handler;
    }

    public Bootstrap bootstrap() throws InterruptedException {
        return new Bootstrap().group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(handler);
    }


    public void shutdown() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }
}
