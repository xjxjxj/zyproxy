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
public abstract class AcceptServer {

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    protected ServerBootstrap bootstrap() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .childHandler(childHandler());

        return bootstrap;
    }

    protected void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    protected abstract ChannelInitializer<SocketChannel> childHandler();

    public abstract void start();
}
