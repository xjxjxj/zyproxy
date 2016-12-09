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
    private final SharaChannels sharaChannels;

    public NetBTPChannel(SharaChannels sharaChannels) {
        super();
        this.sharaChannels = sharaChannels;
    }

    public void channelReadAuth(final ProxyPacket.Auth msg) {
        executeTask(new Runnable() {
            public void run() {
                sharaChannels.putTcpBtpChannel(msg.getAuthCode(), NetBTPChannel.this);
                writeAuth(msg.getAuthCode());
            }
        });
    }
    public void channelReadConnected(final ProxyPacket.Connected msg) {
        executeTask(new Runnable() {
            public void run() {
                getNaturalChannel(msg.getUserCode()).triggerConnectedEvent();
            }
        });
    }

    protected Logger getLogger() {
        return LOGGER;
    }

    public void channelActive() {
        //do nothing
    }
}
