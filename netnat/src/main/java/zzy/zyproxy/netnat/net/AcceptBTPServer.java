package zzy.zyproxy.netnat.net;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.netnat.util.ProxyConfig;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptBTPServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyConfig.class);
    private InetSocketAddress bindAddr;

    public AcceptBTPServer(InetSocketAddress bindAddr) {
        this.bindAddr = bindAddr;
        if (bindAddr == null) {
            throw new RuntimeException("bindAddr不能为null");
        }
    }

    protected ChannelInitializer<SocketChannel> childHandler() {
        return null;
    }

    public void start() {
        try {
            ChannelFuture channelFuture = bootstrap(bindAddr);
            LOGGER.info("AcceptBTPServer bootstrap@port: {}", bindAddr.getPort());
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            shutdown();
        }
    }

}
