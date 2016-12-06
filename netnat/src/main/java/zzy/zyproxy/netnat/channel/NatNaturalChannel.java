package zzy.zyproxy.netnat.channel;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public class NatNaturalChannel extends NaturalChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatNaturalChannel.class);

    public ChannelFuture writeToBTPChannelConnected() {
        BTPChannel btpChannel = BTPChannel();
        if (btpChannel == null) {
            return null;
        }
        return btpChannel.writeConnected(userCode());
    }

    public ChannelFuture writeToBTPChannelTransmit(byte[] msgBody) {
        BTPChannel btpChannel = BTPChannel();
        if (btpChannel == null) {
            return null;
        }
        return btpChannel.writeTransmit(userCode(), msgBody);
    }

    public ChannelFuture writeToBTPChannelClose() {
        BTPChannel btpChannel = BTPChannel();
        if (btpChannel == null) {
            return null;
        }
        return btpChannel.writeClose(userCode());
    }
}
