package zzy.zyproxy.netnat.netsrv;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.util.Balancer;
import zzy.zyproxy.core.util.ChannelUtil;
import zzy.zyproxy.netnat.netsrv.channel.UserNatBTPChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    Balancer balancer = new Balancer();

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
                    LOGGER.info("addUserNatBTPChannel success,acptUserPort:{}【完成注册】", acptUserPort);
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
        Runnable runnable = new Runnable() {
            public void run() {
                takeUserToNatChannel0(channel, localAdd, callable);
            }
        };
        taskExecutor.execute(runnable);

    }

    private void takeUserToNatChannel0(final Channel channel,
                                       final InetSocketAddress localAdd,
                                       final takeUserToNatChannelCallable callable) {
        UserNatBTPChannel userNatBTPChannel = null;
        UserNatBTPChannel.UserChannel userChannel = null;
        try {
            LOGGER.debug("takeUserToNatChannel#run");
            int port = localAdd.getPort();
            HashSet<UserNatBTPChannel> userNatBTPChannelHashSet = userNatChannelMap.get(String.valueOf(port));
            if (userNatBTPChannelHashSet == null) {
                ChannelUtil.closeOnFlush(channel);
                return;
            }

            userNatBTPChannel = balancer.roundRobin(userNatBTPChannelHashSet);
        } catch (Exception e) {
            channel.close();
        } finally {
            LOGGER.debug("callable.call，{}", userNatBTPChannel);
            if (userNatBTPChannel != null) {
                userChannel = userNatBTPChannel.newUserChannel(channel);
            }
            callable.call(userChannel);
        }
    }
}
