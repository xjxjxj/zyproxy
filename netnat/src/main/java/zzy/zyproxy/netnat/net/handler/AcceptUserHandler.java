package zzy.zyproxy.netnat.net.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.handler.NaturalInboundHandler;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public class AcceptUserHandler extends NaturalInboundHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptUserHandler.class);

    public AcceptUserHandler(NaturalChannel naturalChannel) {
        super(naturalChannel);
    }


    protected void active(final NaturalChannel naturalChannel) {
        naturalChannel
                .writeToBTPChannelConnected(new Runnable() {
                    public void run() {
                        try {
                            naturalChannel.ctxRead();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
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
