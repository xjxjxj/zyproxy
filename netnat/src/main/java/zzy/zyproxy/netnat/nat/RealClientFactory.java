package zzy.zyproxy.netnat.nat;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.server.Clienter;
import zzy.zyproxy.netnat.nat.tasker.TcpRealTasker;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author zhouzhongyuan
 * @date 2016/12/7
 */
public class RealClientFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(RealClientFactory.class);
    private final InetSocketAddress realAddr;
    private final Clienter clienter;
    EventLoopGroup group = new NioEventLoopGroup();
    private final Queue<TcpRealTasker> tcpRealTaskerQueue = new LinkedList<TcpRealTasker>();

    public RealClientFactory(InetSocketAddress realAddr) {
        if (realAddr == null) {
            throw new NullPointerException("RealClientFactory#realAddr");
        }
        this.realAddr = realAddr;
        this.clienter = new Clienter(group, new Initializer());
    }


    public synchronized ChannelFuture createClient() throws InterruptedException {
        return clienter.bootstrap().connect(realAddr);
    }

    public void addTcpRealTaskerQueue(TcpRealTasker tcpRealTasker) {
        tcpRealTaskerQueue.add(tcpRealTasker);
    }

    class Initializer extends ChannelInitializer<SocketChannel> {

        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new ByteArrayDecoder(), new ByteArrayEncoder());

            //--
            TcpRealTasker tasker = tcpRealTaskerQueue.poll();
            //--
            pipeline.addLast(new RealHandler(tasker));
        }
    }

    class RealHandler extends SimpleChannelInboundHandler<byte[]> {
        private volatile TcpRealTasker tasker;

        public RealHandler(TcpRealTasker tasker) {
            super();
            if (tasker == null) {
                throw new NullPointerException("RealHandler#tasker");
            }
            this.tasker = tasker;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            tasker.channelActiveEvent(ctx);
        }


        protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
            tasker.channelReadEvent(ctx, msg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            tasker.channelInactiveEvent(ctx);
        }


        @Override
        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
            tasker.channelWritabilityChangedEvent(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOGGER.warn("{}", cause);
        }
    }
}
