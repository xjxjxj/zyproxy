package zzy.zyproxy.netnat;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/12/1
 */
public class Test {
    private final static Logger LOGGER = LoggerFactory.getLogger(Test.class);

    public static void main(String[] agrs) {
        new Test().main();
    }

    public void main() {
        Executor executor = Executors.newCachedThreadPool();

        final InetSocketAddress local = new InetSocketAddress("127.0.0.1", 2566);
        final InetSocketAddress remote = new InetSocketAddress("127.0.0.1", 8888);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    clientBootstrap().connect(remote, local).addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            Channel channel = future.getChannel();
                            channel.getCloseFuture().addListener(new ChannelFutureListener() {
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    synchronized (Test.this) {
                                        Test.this.notifyAll();
                                    }
                                }
                            });
                            while (true) {
                                if (future.getChannel().isConnected()) break;
                            }
                            Channels.close(channel);
                            channel.write(ChannelBuffers.EMPTY_BUFFER);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable runnable1 = new Runnable() {
            public void run() {
                try {
                    synchronized (Test.this) {
                        Test.this.wait();
                        System.out.println("========");
                        clientBootstrap().connect(remote,local).addListener(new ChannelFutureListener() {
                            public void operationComplete(ChannelFuture future) throws Exception {
                                Channel channel = future.getChannel();
                                channel.getCloseFuture();
                                while (true) {
                                    if (future.getChannel().isConnected()) break;
                                }
                                Channels.close(channel);
                                channel.write(ChannelBuffers.EMPTY_BUFFER);
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        executor.execute(runnable);
        executor.execute(runnable1);
    }

    public ClientBootstrap clientBootstrap() {
        Executor executor = Executors.newCachedThreadPool();
        ClientSocketChannelFactory cf = new NioClientSocketChannelFactory(executor, executor);
        final ClientBootstrap cb = new ClientBootstrap(cf);
        cb.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new MyHandler());
            }
        });
        cb.setOption("reuseAddress", true);
        cb.setOption("child.reuseAddress", true);
        return cb;
    }

    class MyHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler {
        public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
            System.out.println("handleDownstream | " + ctx.getChannel() + " | " + e + " | " + this);
            ctx.sendDownstream(e);
        }

        public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
            System.out.println("handleUpstream | " + ctx.getChannel() + " | " + e + " | " + this);
        }
    }
}
