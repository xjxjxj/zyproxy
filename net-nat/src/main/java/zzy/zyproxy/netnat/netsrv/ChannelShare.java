package zzy.zyproxy.netnat.netsrv;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.netnat.netsrv.channel.UserNatBTPChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
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

    private volatile HashMap<String, HashSet<UserNatBTPChannel>> userNatChannelMap
        = new HashMap<String, HashSet<UserNatBTPChannel>>();

    public synchronized void addUserNatBTPChannel(final UserNatBTPChannel userNatBTPChannel, final int acptUserPort) {
        final String port = String.valueOf(acptUserPort);
        HashSet<UserNatBTPChannel> userNatBTPChannels = userNatChannelMap.get(port);
        if (userNatBTPChannels == null) {
            userNatChannelMap.put(port, new HashSet<UserNatBTPChannel>());
        }
        taskExecutor.submit(new Runnable() {
            public void run() {
                try {
                    userNatChannelMap.get(port).add(userNatBTPChannel);
                    LOGGER.debug("addUserNatBTPChannel success,acptUserPort:{}【完成注册】", acptUserPort);
                } catch (Exception e) {
                    userNatBTPChannel.close();
                    e.printStackTrace();
                }
            }
        });
    }

    public interface takeUserToNatChannelCallable {
        void call(UserNatBTPChannel.UserChannel userChannel);
    }

    public void takeUserToNatChannel(final Channel channel,
                                     final InetSocketAddress localAdd,
                                     final takeUserToNatChannelCallable callable) {
        Runnable userToNatTask = new Runnable() {
            public void run() {
                UserNatBTPChannel userNatBTPChannel = null;
                UserNatBTPChannel.UserChannel userChannel = null;
                try {
                    LOGGER.debug("takeUserToNatChannel#run");
                    int port = localAdd.getPort();
                    HashSet<UserNatBTPChannel> userNatBTPChannelHashSet = userNatChannelMap.get(String.valueOf(port));
                    if (userNatBTPChannelHashSet == null) {
                        throw new RuntimeException("不能找到[" + port + "]端口对应的后端服务");
                    }
                    int userSize = 0;
                    for (UserNatBTPChannel aUserNatBTPChannel : userNatBTPChannelHashSet) {
                        int size = aUserNatBTPChannel.getUserSize();
                        if (size <= userSize) {
                            userNatBTPChannel = aUserNatBTPChannel;
                            userSize = size;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    LOGGER.debug("callable.call，{}", userNatBTPChannel);
                    if (userNatBTPChannel != null) {
                        userChannel = userNatBTPChannel.newUserChannel(channel);
                    }
                    callable.call(userChannel);
                }
            }
        };
        taskExecutor.submit(userToNatTask);
    }
}
