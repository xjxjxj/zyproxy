package zzy.zyproxy.netnat.net;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.msgpacket.MsgPackCodec;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.netnat.util.ProxyConfig;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptUserServer extends AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyConfig.class);
    private InetSocketAddress bindAddr;

    public AcceptUserServer(InetSocketAddress bindAddr) {
        this.bindAddr = bindAddr;
        if (bindAddr == null) {
            throw new RuntimeException("bindAddr不能为null");
        }
    }

    protected ChannelInitializer<SocketChannel> childHandler() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                MsgPackCodec.addCodec(pipeline);
            }
        };
    }

    public void start() {
        try {
            ChannelFuture channelFuture = bootstrap(bindAddr);
            LOGGER.info("AcceptUserServer bootstrap@port: {}", bindAddr.getPort());
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            shutdown();
        }
    }

}
