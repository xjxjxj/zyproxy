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
    private final Queue<NatNaturalChannel> usersQueue = new LinkedList<NatNaturalChannel>();
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


    public synchronized NatNaturalChannel pollNatNaturalChannel() {
        return usersQueue.poll();
    }

    protected Runnable channelActiveRunnable() {
        return new Runnable() {
            public void run() {
                writeAuth(auth);
            }
        };
    }

    public Runnable channelReadAuthRunnable(final ProxyPacket.Auth msg) {
        return new Runnable() {
            public void run() {
                if (!auth.equals(msg.getAuthCode())) {
                    flushAndClose();
                }
            }
        };
    }


    public Runnable channelReadConnectedRunnable(final ProxyPacket.Connected msg) {
        return new Runnable() {
            public void run() {
                Integer userCode = msg.getUserCode();
                NatNaturalChannel natNaturalChannel = new NatNaturalChannel(NatBTPChannel.this, userCode);
                putNaturalChannel(userCode, natNaturalChannel);
                usersQueue.add(natNaturalChannel);
                realClientFactory.createClient();
            }
        };
    }

    protected Logger subLogger() {
        return LOGGER;
    }
}
