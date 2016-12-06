package zzy.zyproxy.netnat.net;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.msgpacket.MsgPackCodec;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.netnat.channel.NatNaturalChannel;
import zzy.zyproxy.netnat.net.handler.AcceptUserHandler;
import zzy.zyproxy.netnat.util.NatSharaChannels;
import zzy.zyproxy.netnat.util.ProxyConfig;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptUserServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyConfig.class);
    private InetSocketAddress bindAddr;
    private final NatSharaChannels natSharaChannels;

    public AcceptUserServer(InetSocketAddress bindAddr,NatSharaChannels natSharaChannels) {
        this.bindAddr = bindAddr;
        this.natSharaChannels = natSharaChannels;
        if (bindAddr == null) {
            throw new RuntimeException("bindAddr不能为null");
        }
    }

    protected ChannelInitializer<SocketChannel> childHandler() {
        return new Initializer();
    }

    public void start() {
        try {
            LOGGER.info("AcceptUserServer bootstrap@port: {}", bindAddr.getPort());

            ChannelFuture channelFuture = bootstrap(bindAddr);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    class Initializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            MsgPackCodec.addCodec(pipeline);
            pipeline.addLast(new AcceptUserHandler(
                new NatNaturalChannel(),
                natSharaChannels,                
                bindAddr.getPort()
            ));
        }
    }

}
