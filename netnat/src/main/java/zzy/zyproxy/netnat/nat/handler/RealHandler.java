package zzy.zyproxy.netnat.nat.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.handler.NaturalInboundHandler;

/**
 * @author zhouzhongyuan
 * @date 2016/12/7
 */
public class RealHandler extends NaturalInboundHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(RealHandler.class);

    public RealHandler() {
        super(null);
    }

    protected void active(final NaturalChannel naturalChannel) {
        naturalChannel.writeToBTPChannelConnected().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    naturalChannel.ctxRead();
                } else {
                    naturalChannel.flushAndClose();
                }
            }
        });
    }

    protected void read(final NaturalChannel naturalChannel, byte[] msg) {
        naturalChannel.writeToBTPChannelTransmit(msg).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    naturalChannel.ctxRead();
                } else {
                    naturalChannel.flushAndClose();
                }
            }
        });
    }

    protected void inActive(NaturalChannel naturalChannel) {
        naturalChannel.writeToBTPChannelClose();
    }

    protected void writabilityChanged(NaturalChannel naturalChannel) {

    }
}
