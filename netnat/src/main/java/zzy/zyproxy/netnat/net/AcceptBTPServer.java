package zzy.zyproxy.netnat.net;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.msgpacket.MsgPackCodec;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.netnat.channel.NatBTPChannel;
import zzy.zyproxy.netnat.net.handler.AcceptBTPHandler;
import zzy.zyproxy.netnat.util.NatSharaChannels;
import zzy.zyproxy.netnat.util.ProxyConfig;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptBTPServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyConfig.class);
    private final NatSharaChannels natSharaChannels;
    private InetSocketAddress bindAddr;

    public AcceptBTPServer(InetSocketAddress bindAddr,NatSharaChannels natSharaChannels) {
        this.bindAddr = bindAddr;
        if (bindAddr == null) {
            throw new RuntimeException("bindAddr不能为null");
        }
        this.natSharaChannels = natSharaChannels;
    }

    protected ChannelInitializer<SocketChannel> childHandler() {
        return new Initializer();
    }

    public void start() {
        try {
            ChannelFuture channelFuture = bootstrap(bindAddr);
            LOGGER.info("AcceptBTPServer bootstrap@port: {}", bindAddr.getPort());
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    class Initializer extends ChannelInitializer<SocketChannel> {
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            MsgPackCodec.addCodec(pipeline);
            pipeline.addLast(new AcceptBTPHandler(
                new NatBTPChannel(),
                natSharaChannels
            ));
        }
    }

}
