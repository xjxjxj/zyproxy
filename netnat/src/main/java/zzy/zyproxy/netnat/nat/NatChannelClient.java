package zzy.zyproxy.netnat.nat;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.msgpacket.MsgPackCodec;
import zzy.zyproxy.core.server.ChannelClient;
import zzy.zyproxy.netnat.channel.NatBTPChannel;
import zzy.zyproxy.netnat.nat.handler.NatBTPHandler;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public class NatChannelClient extends ChannelClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatChannelClient.class);
    private final InetSocketAddress acceptBTPAddr;
    private final String auth;
    private final RealClientFactory realClientFactory;

    public NatChannelClient(InetSocketAddress acceptBTPAddr, InetSocketAddress realAddr, String auth) {

        this.acceptBTPAddr = acceptBTPAddr;
        this.auth = auth;
        this.realClientFactory = new RealClientFactory(realAddr);
    }

    protected ChannelInitializer<SocketChannel> handler() {
        return new Initializer();
    }

    public void start() {
        ChannelFuture channelFuture;
        try {
            channelFuture = bootstrap().connect(acceptBTPAddr).sync();
            LOGGER.info("NatChannelClient@{},auth:{}",acceptBTPAddr,auth);
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
            pipeline.addLast(new NatBTPHandler(new NatBTPChannel(), auth, realClientFactory));
        }
    }
}
