package zzy.zyproxy.netnat.net;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.msgpacket.MsgPackCodec;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.netnat.channel.NetNatBTPChannel;
import zzy.zyproxy.netnat.net.channel.NetBTPChannel;
import zzy.zyproxy.netnat.net.handler.AcceptBTPHandler;
import zzy.zyproxy.netnat.util.NatSharaChannels;
import zzy.zyproxy.netnat.util.ProxyConfig;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptBTPServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyConfig.class);
    private final NatSharaChannels natSharaChannels;
    private final AcceptServer acceptServer;
    private InetSocketAddress bindAddr;

    public AcceptBTPServer(InetSocketAddress bindAddr, NatSharaChannels natSharaChannels) {
        if (bindAddr == null) {
            throw new NullPointerException("AcceptBTPServer#bindAddr");
        }
        if (natSharaChannels == null) {
            throw new NullPointerException("AcceptBTPServer#natSharaChannels");
        }
        this.bindAddr = bindAddr;
        this.natSharaChannels = natSharaChannels;
        this.acceptServer = new AcceptServer(new NioEventLoopGroup(), new NioEventLoopGroup(), new Initializer());
    }

    public void start() {
        try {
            ChannelFuture channelFuture = acceptServer.bootstrap()
                .option(ChannelOption.AUTO_READ, true)
                .bind(bindAddr);

            LOGGER.info("AcceptBTPServer bootstrap@port: {}", bindAddr.getPort());
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            acceptServer.shutdown();
        }
    }

    class Initializer extends ChannelInitializer<SocketChannel> {
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            MsgPackCodec.addCodec(pipeline);
            //--
            NetBTPChannel netBTPChannel = new NetBTPChannel(natSharaChannels);
            //--
            pipeline.addLast(new AcceptBTPHandler(netBTPChannel));
        }
    }

}
