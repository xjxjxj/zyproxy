package zzy.zyproxy.netnat.net.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
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
    private final Integer userCode;
    private BTPChannel tcpBtpChannel;

    public NetNaturalChannel(NatSharaChannels natSharaChannels, InetSocketAddress bindAddr) {
        super();
        if (natSharaChannels == null) {
            throw new NullPointerException("NetNaturalChannel#natSharaChannels");
        }
        if (bindAddr == null) {
            throw new NullPointerException("NetNaturalChannel#bindAddr");
        }
        this.natSharaChannels = natSharaChannels;
        this.bindAddr = bindAddr;
        this.userCode = this.hashCode();
    }

    public Integer userCode() {
        return userCode;
    }

    public BTPChannel btpChannel() {
        return tcpBtpChannel;
    }

    public void channelActive() {
        submitTask(new Runnable() {
            public void run() {
                LOGGER.debug("运行【1】channelActive【开始】");
                tcpBtpChannel = natSharaChannels.getTcpBtpChannel(bindAddr.getPort());
                btpChannel().putNaturalChannel(userCode(), NetNaturalChannel.this);
                btpChannel().writeConnected(userCode());
                LOGGER.debug("运行【1】channelActive【结束】");
            }
        });
    }


}
