package zzy.zyproxy.netnat.netsrv;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.netnat.netsrv.channel.NetHeartChannel;
import zzy.zyproxy.netnat.netsrv.channel.UserNatBTPChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * @author zhouzhongyuan
 * @date 2016/11/25
 */
public class ChannelShare {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChannelShare.class);

    //############[============]############//
    //############[UserNatChannelMap]############//
    //############[============]############//
    private final ExecutorService taskExecutor = Executors.newCachedThreadPool();

    private volatile HashMap<String, BlockingQueue<UserNatBTPChannel>> userNatChannelMap
        = new HashMap<String, BlockingQueue<UserNatBTPChannel>>();

    public void putNatBTPChannel(final UserNatBTPChannel userNatBTPChannel, final int acptUserPort) {
        final BlockingQueue<UserNatBTPChannel> natBTPChannels = userNatChannelMap.get(String.valueOf(acptUserPort));
        taskExecutor.submit(new Runnable() {
            public void run() {
                try {
                    natBTPChannels.put(userNatBTPChannel);
                    LOGGER.debug("putNatBTPChannel success,acptUserPort:{}", acptUserPort);
                } catch (Exception e) {
                    userNatBTPChannel.close();
                    e.printStackTrace();
                }
            }
        });
    }

    public interface takeUserToNatChannelCallable {
        void call(UserNatBTPChannel userNatBTPChannel);
    }

    public void takeUserToNatChannel(final InetSocketAddress localAdd,
                                     final takeUserToNatChannelCallable callable) {
        Runnable userToNatTask = new Runnable() {
            public void run() {
                UserNatBTPChannel userNatBTPChannel = null;
                try {
                    LOGGER.debug("takeUserToNatChannel#run");
                    int port = localAdd.getPort();
                    BlockingQueue<UserNatBTPChannel> channels = userNatChannelMap.get(String.valueOf(port));
                    if (channels == null) {
                        throw new RuntimeException("不能找到[" + port + "]端口对应的后端服务");
                    }
                    try {
                        userNatBTPChannel = channels.poll(1, TimeUnit.SECONDS);
                        LOGGER.debug("直接从channelPool中取得了backchannel:{}@port:{}", userNatBTPChannel, port);
                    } finally {
                        if (userNatBTPChannel == null) {
                            takeNatChannelFromRemoteAtPort(port);
                            userNatBTPChannel = channels.poll(10, TimeUnit.SECONDS);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    LOGGER.debug("callable.call，{}", userNatBTPChannel);
                    callable.call(userNatBTPChannel);
                }
            }
        };
        taskExecutor.submit(userToNatTask);
    }


    //############[=============]############//
    //############[HeartChannels]############//
    //############[=============]############//
    /**
     * 用来注册Tcp的端口 排除80和443
     */
    private volatile ConcurrentHashMap<Integer, NetHeartChannel> natPortRegister
        = new ConcurrentHashMap<Integer, NetHeartChannel>();

    public void putNewHeartChannel(NetHeartChannel netHeartChannel, Integer netUserProxyPort) {
        if (netUserProxyPort == null) {
            throw new RuntimeException("后台连接池获取本地端口错误");
        }
        NetHeartChannel netHeartChannelReg = natPortRegister.get(netUserProxyPort);
        if (netHeartChannelReg == null || !netHeartChannelReg.isConnected()) {
            natPortRegister.put(netUserProxyPort, netHeartChannel);
            userNatChannelMap.put(netUserProxyPort.toString(), new LinkedBlockingQueue<UserNatBTPChannel>());
        }
        LOGGER.debug("后台Heart连接池putNewHeartChannel,@NetUserProxyPort:{}", netUserProxyPort);
    }

    private void takeNatChannelFromRemoteAtPort(int localAddPort) {
        NetHeartChannel heartchannel = natPortRegister.get(localAddPort);
        if (heartchannel == null) {
            throw new RuntimeException("[" + localAddPort + "]当前端口没有后台服务注册");
        }
        LOGGER.debug("从Lan端进行请求BackChannel@port:{},NetHeartChannel:{}", localAddPort, heartchannel);
        heartchannel.writeReqNatChannel();
    }

    public void newCloseBackSrvTask(Channel channel) {
        LOGGER.debug("newCloseBackSrvTask {}", channel.getId());
    }

}
