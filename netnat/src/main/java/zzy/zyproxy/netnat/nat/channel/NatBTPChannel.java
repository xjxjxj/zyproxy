package zzy.zyproxy.netnat.nat.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.netnat.channel.NetNatBTPChannel;
import zzy.zyproxy.netnat.nat.RealClientFactory;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author zhouzhongyuan
 * @date 2016/12/8
 */
public class NatBTPChannel extends NetNatBTPChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPChannel.class);
    private final Queue<ProxyPacket.Connected> usersQueue = new LinkedList<ProxyPacket.Connected>();
    private final String auth;
    private final RealClientFactory realClientFactory;

    public NatBTPChannel(String auth, InetSocketAddress realAddr) {
        super();
        if (auth == null) {
            throw new NullPointerException("NatBTPChannel#auth");
        }
        if (realAddr == null) {
            throw new NullPointerException("NatBTPChannel#realAddr");
        }
        this.auth = auth;
        this.realClientFactory = new RealClientFactory(this, realAddr);
    }
    

    public synchronized ProxyPacket.Connected pollUser() {
        return usersQueue.poll();
    }

    public void channelActive() {
        writeAuth(auth);
    }

    public void channelReadAuth(ProxyPacket.Auth auth) {
        if (!this.auth.equals(auth.getAuthCode())) {
            flushAndClose();
        }
    }

    public void channelReadConnected(final ProxyPacket.Connected msg) {
        executeTask(new Runnable() {
            public void run() {
                usersQueue.add(msg);
                realClientFactory.createClient();
            }
        });
    }

    protected Logger getLogger() {
        return LOGGER;
    }
}
