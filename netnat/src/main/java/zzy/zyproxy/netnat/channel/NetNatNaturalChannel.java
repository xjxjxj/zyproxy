package zzy.zyproxy.netnat.channel;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.channel.ProxyChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public class NetNatNaturalChannel extends ProxyChannel implements NaturalChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NetNatNaturalChannel.class);

    private final Integer userCode;
    private final BTPChannel btpChannel;
    private volatile Runnable connectedEvent;
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();


    public NetNatNaturalChannel(Integer userCode, BTPChannel btpChannel) {
        this(null, userCode, btpChannel);
    }

    public NetNatNaturalChannel(ChannelHandlerContext ctx, Integer userCode, BTPChannel btpChannel) {
        super(ctx);
        if (userCode == null || btpChannel == null) {
            throw new RuntimeException("Integer userCode, BTPChannel btpChannel 不能为NULL");
        }
        this.userCode = userCode;
        this.btpChannel = btpChannel;
    }

    public Integer userCode() {
        return userCode;
    }

    public BTPChannel BTPChannel() {
        return btpChannel;
    }

    public void regConnectedEvent(Runnable connectedEvent) {
        this.connectedEvent = connectedEvent;
    }

    public void realConnected() {
        if (connectedEvent != null) {
            cachedThreadPool.execute(connectedEvent);
        }
    }

    public ChannelFuture writeMsgAndFlush(byte[] body) {
        return super.writeAndFlush(Unpooled.wrappedBuffer(body));
    }

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
