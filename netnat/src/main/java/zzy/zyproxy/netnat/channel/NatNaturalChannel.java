package zzy.zyproxy.netnat.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
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

    public NatNaturalChannel(String userCode) {
        super(userCode);
    }

    public NatNaturalChannel(ChannelHandlerContext ctx, String userCode) {
        super(ctx, userCode);
    }


    public ChannelFuture writeToBTPChannelConnected(Runnable realConnectedEvent) {
        BTPChannel btpChannel = BTPChannel();
        if (btpChannel == null) {
            return null;
        }
        this.realConnectedEvent = realConnectedEvent;
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
