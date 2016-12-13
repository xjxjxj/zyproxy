package zzy.zyproxy.netnat.nat;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.packet.msgpacket.MsgPackCodec;
import zzy.zyproxy.core.server.Clienter;
import zzy.zyproxy.core.util.SharaChannels;
import zzy.zyproxy.core.util.task.TaskExecutors;
import zzy.zyproxy.netnat.nat.tasker.ClientBTPTasker;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public class NatBTPClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPClient.class);
    private final InetSocketAddress acceptBTPAddr;
    private final String auth;
    private final TaskExecutors taskExecutors;
    private final Clienter clienter;
    private final RealClientFactory realClientFactory;
    private final SharaChannels sharaChannels;

    public NatBTPClient(InetSocketAddress acceptBTPAddr, InetSocketAddress realAddr, String auth, TaskExecutors taskExecutors) {
        if (acceptBTPAddr == null) {
            throw new NullPointerException("NatBTPHandler#acceptBTPAddr");
        }
        if (realAddr == null) {
            throw new NullPointerException("NatBTPHandler#realAddr");
        }
        if (auth == null) {
            throw new NullPointerException("NatBTPHandler#auth");
        }
        if (taskExecutors == null) {
            throw new NullPointerException("NatBTPHandler#taskExecutors");
        }
        //==
        this.acceptBTPAddr = acceptBTPAddr;
        this.auth = auth;
        this.taskExecutors = taskExecutors;
        clienter = new Clienter(new NioEventLoopGroup(), new Initializer());
        this.sharaChannels = new NatSharaChannels();
        this.realClientFactory = new RealClientFactory(realAddr);
    }

    public void start() {
        ChannelFuture channelFuture;
        try {
            channelFuture = clienter.bootstrap().connect(acceptBTPAddr).sync();
            LOGGER.info("NatBTPClient@{},auth:{}", acceptBTPAddr, auth);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clienter.shutdown();
        }
    }

    class Initializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            MsgPackCodec.addCodec(pipeline);

            ClientBTPTasker clientBTPTasker
                = new ClientBTPTasker(sharaChannels, realClientFactory, auth, taskExecutors);

            pipeline.addLast(new NatBTPHandler(clientBTPTasker));
        }
    }

    class NatBTPHandler extends SimpleChannelInboundHandler<ProxyPacket> {
        private final ClientBTPTasker tasker;

        public NatBTPHandler(ClientBTPTasker tasker) {
            super();
            if (tasker == null) {
                throw new NullPointerException("NatBTPHandler#natBTPChannel");
            }
            this.tasker = tasker;
        }


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            tasker.channelActiveEvent(ctx);
        }


        protected void channelRead0(ChannelHandlerContext ctx, ProxyPacket msg) throws Exception {
            tasker.channelReadEvent(ctx, msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOGGER.warn("{}", this.getClass().getSimpleName(), cause);
        }
    }
}
