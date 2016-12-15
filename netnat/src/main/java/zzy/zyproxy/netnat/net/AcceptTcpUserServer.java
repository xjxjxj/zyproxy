package zzy.zyproxy.netnat.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.server.AcceptServer;
import zzy.zyproxy.core.util.ShareChannels;
import zzy.zyproxy.core.util.task.TaskExecutors;
import zzy.zyproxy.netnat.net.tasker.AcceptTcpUserTasker;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptTcpUserServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptTcpUserServer.class);
    private final AcceptServer acceptServer;
    private final TaskExecutors taskExecutors;
    private InetSocketAddress bindAddr;
    private final ShareChannels shareChannels;

    public AcceptTcpUserServer(InetSocketAddress bindAddr, ShareChannels shareChannels, TaskExecutors taskExecutors) {
        if (bindAddr == null) {
            throw new NullPointerException("AcceptTcpUserServer#bindAddr");
        }
        if (shareChannels == null) {
            throw new NullPointerException("AcceptTcpUserServer#shareChannels");
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
            ChannelFuture channelFuture
                = acceptServer.bootstrap()
                .childOption(ChannelOption.AUTO_READ, true)
                .bind(bindAddr);
            LOGGER.info("AcceptTcpUserServer bootstrap@port: {}", bindAddr.getPort());
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
            //-
            AcceptTcpUserTasker tasker
                = new AcceptTcpUserTasker(shareChannels, bindAddr, taskExecutors);
            //-
            pipeline.addLast(new AcceptUserHandler(tasker));
        }
    }

    class AcceptUserHandler extends ChannelInboundHandlerAdapter {
        private final AcceptTcpUserTasker tasker;

        public AcceptUserHandler(AcceptTcpUserTasker tasker) {
            super();
            if (tasker == null) {
                throw new NullPointerException("AcceptUserHandler#tasker");
            }
            this.tasker = tasker;
        }


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            tasker.channelActiveEvent(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (!(msg instanceof ByteBuf)) {
                return;
            }
            ByteBuf buf = (ByteBuf) msg;
            byte[] array = new byte[buf.readableBytes()];
            buf.getBytes(0, array);
            channelRead0(ctx, array);
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
