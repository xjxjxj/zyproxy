package zzy.zyproxy.netnat.net.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.netnat.channel.NetNatNaturalChannel;
import zzy.zyproxy.netnat.util.NatSharaChannels;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/9
 */
public class NetNaturalChannel extends NetNatNaturalChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NetNaturalChannel.class);
    private final NatSharaChannels natSharaChannels;
    private final InetSocketAddress bindAddr;


    public NetNaturalChannel(NatSharaChannels natSharaChannels, InetSocketAddress bindAddr) {
        super();
        this.natSharaChannels = natSharaChannels;
        this.bindAddr = bindAddr;
        setUserCode(this.hashCode());
    }


    public void channelActive() {
        executeTask(new Runnable() {
            public void run() {
                setBTPChannel(natSharaChannels.getTcpBtpChannel(bindAddr.getPort()));
                btpChannel().putNaturalChannel(userCode(), NetNaturalChannel.this);
                regConnectedEvent(new Runnable() {
                    public void run() {
                        ctxRead();
                    }
                });
                btpChannel().writeConnected(userCode());
            }
        });
    }


}
