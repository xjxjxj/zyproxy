package zzy.zyproxy.netnat.net;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.handler.InboundHandlerEvent;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.packet.msgpacket.MsgPackCodec;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.core.util.ShareChannels;
import zzy.zyproxy.core.util.task.TaskExecutors;
import zzy.zyproxy.netnat.net.tasker.AcceptBTPTasker;
import zzy.zyproxy.netnat.util.ProxyConfig;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptBTPServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyConfig.class);
    private final ShareChannels shareChannels;
    private final AcceptServer acceptServer;
    private final TaskExecutors taskExecutors;
    private InetSocketAddress bindAddr;

    public AcceptBTPServer(InetSocketAddress bindAddr, ShareChannels shareChannels, TaskExecutors taskExecutors) {
        if (bindAddr == null) {
            throw new NullPointerException("AcceptBTPServer#bindAddr");
        }
        if (shareChannels == null) {
            throw new NullPointerException("AcceptBTPServer#natShareChannels");
        }
        if (taskExecutors == null) {
            throw new NullPointerException("AcceptBTPServer#taskExecutors");
        }
        this.taskExecutors = taskExecutors;
        this.bindAddr = bindAddr;
        this.shareChannels = shareChannels;
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
            AcceptBTPTasker acceptBTPTasker = new AcceptBTPTasker(shareChannels,taskExecutors);

            pipeline.addLast(new AcceptBTPHandler(acceptBTPTasker));
        }
    }

    ///===AcceptBTPHandler
    class AcceptBTPHandler extends SimpleChannelInboundHandler<ProxyPacket> {
        private final InboundHandlerEvent<ProxyPacket> inboundHandlerEvent;

        public AcceptBTPHandler(InboundHandlerEvent<ProxyPacket> inboundHandlerEvent) {
            super();
            if (inboundHandlerEvent == null) {
                throw new NullPointerException("AcceptBTPHandler#inboundHandlerEvent");
            }
            this.inboundHandlerEvent = inboundHandlerEvent;
        }

        protected void channelRead0(ChannelHandlerContext ctx, ProxyPacket msg) throws Exception {
            inboundHandlerEvent.channelReadEvent(ctx, msg);

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOGGER.warn("{}", cause);
        }
    }

}
