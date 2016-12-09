package zzy.zyproxy.netnat.net;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.netnat.channel.NetNatNaturalChannel;
import zzy.zyproxy.netnat.net.channel.NetNaturalChannel;
import zzy.zyproxy.netnat.net.handler.AcceptUserHandler;
import zzy.zyproxy.netnat.util.NatSharaChannels;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptUserServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptUserServer.class);
    private final AcceptServer acceptServer;
    private InetSocketAddress bindAddr;
    private final NatSharaChannels natSharaChannels;

    public AcceptUserServer(InetSocketAddress bindAddr, NatSharaChannels natSharaChannels) {
        if (bindAddr == null) {
            throw new NullPointerException("AcceptUserServer#bindAddr");
        }
        if (natSharaChannels == null) {
            throw new NullPointerException("AcceptUserServer#natSharaChannels");
        }
        this.bindAddr = bindAddr;
        this.natSharaChannels = natSharaChannels;
        this.acceptServer = new AcceptServer(new NioEventLoopGroup(), new NioEventLoopGroup(), new Initializer());
    }

    public void start() {
        try {
            ChannelFuture channelFuture = acceptServer.bootstrap()
                .childOption(ChannelOption.AUTO_READ, false)
                .bind(bindAddr);
            LOGGER.info("AcceptUserServer bootstrap@port: {}", bindAddr.getPort());
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            acceptServer.shutdown();
        }
    }

    class Initializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            NaturalChannel naturalChannel = new NetNaturalChannel(natSharaChannels, bindAddr);
            pipeline.addLast(new AcceptUserHandler(naturalChannel));
        }
    }

}
