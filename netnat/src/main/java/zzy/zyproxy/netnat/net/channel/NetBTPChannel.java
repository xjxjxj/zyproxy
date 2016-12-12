package zzy.zyproxy.netnat.net.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.SharaChannels;
import zzy.zyproxy.netnat.channel.NetNatBTPChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/9
 */
public class NetBTPChannel extends NetNatBTPChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NetBTPChannel.class);

    protected Logger subLogger() {
        return LOGGER;
    }

    private final SharaChannels sharaChannels;

    public NetBTPChannel(SharaChannels sharaChannels) {
        super();
        this.sharaChannels = sharaChannels;
    }


    public Runnable channelReadConnectedRunnable(final ProxyPacket.Connected msg) {
        return new Runnable() {
            public void run() {
                //do noting
            }
        };
    }


    public Runnable channelReadAuthRunnable(final ProxyPacket.Auth msg) {
        return new Runnable() {
            public void run() {
                sharaChannels.putTcpBtpChannel(msg.getAuthCode(), NetBTPChannel.this);
                writeAuth(msg.getAuthCode());
            }
        };
    }


    protected Runnable channelActiveRunnable() {
        return new Runnable() {
            public void run() {
                //do nothing
            }
        };
    }
}
