package zzy.zyproxy.core.server;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public abstract class AcceptServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptServer.class);
    ServerBootstrap bootstrap;

    public void start() {
        Channel channel = null;
        try {
            // Configure the server.
            bootstrap = new ServerBootstrap();
            bootstrap.setFactory(getChannelFactory());
            bootstrap.setPipelineFactory(getPipelineFactory());
            bootstrap.setOptions(getOptions());

            // Bind and start to accept incoming connections.
            channel = bootstrap.bind(getInetSocketAddress());
            channel.getCloseFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.close();
            }
            stop();
        }
    }

    protected abstract SocketAddress getInetSocketAddress();

    protected abstract ChannelPipelineFactory getPipelineFactory();

    protected abstract ChannelFactory getChannelFactory();

    public void stop() {
        bootstrap.shutdown();
    }

    protected Map<String, Object> getOptions() {
        return new HashMap<String, Object>() {{
            put("child.tcpNoDelay", true);
        }};
    }
}