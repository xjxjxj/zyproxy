package zzy.zyproxy.netnat.nat.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.netnat.channel.NetNatNaturalChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/9
 */
public class NatNaturalChannel extends NetNatNaturalChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatNaturalChannel.class);
    private final BTPChannel btpChannel;
    private final Integer userCode;

    public NatNaturalChannel(NatBTPChannel btpChannel, Integer userCode) {
        super();
        if (btpChannel == null) {
            throw new NullPointerException("NatNaturalChannel#NatBTPChannel");
        }
        if (userCode == null) {
            throw new NullPointerException("NatNaturalChannel#userCode");
        }
        this.btpChannel = btpChannel;
        this.userCode = userCode;
    }

    public Integer userCode() {
        return userCode;
    }

    public BTPChannel btpChannel() {
        return btpChannel;
    }

    public void channelActive() {
        submitTask(new Runnable() {
            public void run() {
                btpChannel().writeConnected(userCode());
            }
        });
    }


}
