package zzy.zyproxy.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public class AcceptServer {
    private final NioEventLoopGroup bossGroup;
    private final NioEventLoopGroup workerGroup;

    private final ChannelInitializer<SocketChannel> childHandler;

    public AcceptServer(NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup, ChannelInitializer<SocketChannel> childHandler) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.childHandler = childHandler;
    }

    public ServerBootstrap bootstrap() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .childHandler(childHandler);

        return bootstrap;
    }

    public void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
