package zzy.zyproxy.netnat.nat;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.server.ChannelClient;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public class NatChannelClient extends ChannelClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatChannelClient.class);
    private final InetSocketAddress acceptBTPAddr;
    private final InetSocketAddress realAddr;

    public NatChannelClient(InetSocketAddress acceptBTPAddr, InetSocketAddress realAddr) {

        this.acceptBTPAddr = acceptBTPAddr;
        this.realAddr = realAddr;
    }

    protected ChannelInitializer<SocketChannel> handler() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                System.out.println(ch);
                
            }
        };
    }

    public void start() {
        ChannelFuture channelFuture;
        try {
            channelFuture = bootstrap(acceptBTPAddr);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }
}
