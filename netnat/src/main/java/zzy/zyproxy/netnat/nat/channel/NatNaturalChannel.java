package zzy.zyproxy.netnat.nat.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.netnat.channel.NetNatNaturalChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/9
 */
public class NatNaturalChannel extends NetNatNaturalChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatNaturalChannel.class);

    public NatNaturalChannel(NatBTPChannel btpChannel) {
        super();
        if (btpChannel == null) {
            throw new NullPointerException("NatNaturalChannel#NatBTPChannel");
        }
        setBTPChannel(btpChannel);
    }

    public void channelActive() {
        executeTask(new Runnable() {
            public void run() {
                NatBTPChannel btpChannel = (NatBTPChannel) btpChannel();
                ProxyPacket.Connected connected = btpChannel.pollUser();
                setUserCode(connected.getUserCode());
                btpChannel.putNaturalChannel(userCode(), NatNaturalChannel.this);
                btpChannel().writeConnected(userCode());
                ctxRead();
            }
        });
    }


}
