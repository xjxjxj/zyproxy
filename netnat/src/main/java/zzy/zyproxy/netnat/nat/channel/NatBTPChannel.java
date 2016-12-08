package zzy.zyproxy.netnat.nat.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.netnat.channel.NetNatBTPChannel;
import zzy.zyproxy.netnat.channel.NetNatNaturalChannel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zhouzhongyuan
 * @date 2016/12/8
 */
public class NatBTPChannel extends NetNatBTPChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPChannel.class);
    private final BlockingQueue<NetNatNaturalChannel> usersQueue = new LinkedBlockingQueue<NetNatNaturalChannel>();


    public BlockingQueue<NetNatNaturalChannel> getUsersQueue() {
        return usersQueue;
    }

    public NaturalChannel pollUser() {
        System.out.println(usersQueue.size());
        System.out.println("pollUser 【开始】");
        NetNatNaturalChannel naturalChannel = usersQueue.poll();
        super.putNaturalChannel(naturalChannel.userCode(), naturalChannel);
        System.out.println("pollUser 【结束】");
        return naturalChannel;
    }
}
