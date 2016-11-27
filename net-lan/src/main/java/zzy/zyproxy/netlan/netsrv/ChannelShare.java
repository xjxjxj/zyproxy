package zzy.zyproxy.netlan.netsrv;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.netlan.netsrv.channel.NetHeartChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author zhouzhongyuan
 * @date 2016/11/25
 */
public class ChannelShare {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChannelShare.class);

    //############[============]############//
    //############[backChannels]############//
    //############[============]############//
    private final Queue<Runnable> tasks = new ArrayBlockingQueue<Runnable>(1000);
    private final ExecutorService taskExecutor = Executors.newCachedThreadPool();
    private final ScheduledExecutorService checkBackProxyChannelsExecutor = Executors.newSingleThreadScheduledExecutor();

    private volatile HashMap<String, BlockingQueue<Channel>> backChannelMap = new HashMap<String, BlockingQueue<Channel>>();

    public void takeUserToBackChannel(Channel channel, InetSocketAddress localAdd) {
        int port = localAdd.getPort();
        BlockingQueue<Channel> channels = backChannelMap.get(String.valueOf(port));
        if (channels == null) {
            throw new RuntimeException("不能找到[" + port + "]端口对应的后端服务");
        }
        Channel pollChannel = null;
        try {
            pollChannel = channels.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (pollChannel == null) {
                takeBackChannelFromRemoteAtPort(port);
            }
        }
    }

    //############[=============]############//
    //############[HeartChannels]############//
    //############[=============]############//
    /**
     * 用来注册Tcp的端口 排除80和443
     */
    private volatile ConcurrentHashMap<Integer, NetHeartChannel> portRegister = new ConcurrentHashMap<Integer, NetHeartChannel>();

    public void putNewHeartChannel(NetHeartChannel netHeartChannel, Integer proxyPort) {
        if (proxyPort == null) {
            throw new RuntimeException("后台连接池获取本地端口错误");
        }
        NetHeartChannel netHeartChannelReg = portRegister.get(proxyPort);
        if (netHeartChannelReg == null || !netHeartChannelReg.isConnected()) {
            portRegister.put(proxyPort, netHeartChannel);
        }
        LOGGER.debug("后台Heart连接池putNewHeartChannel,@proxyPort:{}");
    }

    private void takeBackChannelFromRemoteAtPort(int localAddPort) {
        NetHeartChannel backHeartchannel = portRegister.get(localAddPort);
        if (backHeartchannel == null) {
            throw new RuntimeException("[" + localAddPort + "]当前端口没有后台服务注册");
        }
        backHeartchannel.writeReqBackChannel();
    }

    public void newCloseBackSrvTask(Channel channel) {
        LOGGER.debug("newCloseBackSrvTask {}", channel.getId());
    }

}
