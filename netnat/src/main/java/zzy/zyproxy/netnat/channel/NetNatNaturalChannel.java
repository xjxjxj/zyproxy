package zzy.zyproxy.netnat.channel;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.channel.ProxyChannel;
import zzy.zyproxy.core.util.TaskExecutor;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public abstract class NetNatNaturalChannel extends ProxyChannel implements NaturalChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NetNatNaturalChannel.class);

    public enum Status {}

    public NetNatNaturalChannel() {
        super();
    }

    public ChannelFuture writeMsgAndFlush(byte[] body) {
        return super.writeAndFlush(Unpooled.wrappedBuffer(body));
    }

    //--
    public abstract Integer userCode();

    //---
    private final TaskExecutor taskQueue = TaskExecutor.createQueueExecuter();

    public void submitTask(final Runnable runnable) {
        taskQueue.submitTask(runnable);
    }

    //==

    public abstract BTPChannel btpChannel();


    public abstract void channelActive();

    public void channelRead(final byte[] bytes) {
        submitTask(new Runnable() {
            public void run() {
                LOGGER.debug("运行【2】channelRead and writeTransmit【开始】");
                btpChannel()
                    .writeTransmit(userCode(), bytes);
                LOGGER.debug("运行【2】channelRead and writeTransmit【结束】");
            }
        });
    }

    public void channelInactive() {
        submitTask(new Runnable() {
            public void run() {
                btpChannel()
                    .writeClose(userCode())
                    .addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            btpChannel().removeNaturalChannel(userCode());
                        }
                    });
            }
        });
    }

    public void channelWritabilityChanged() {
        //TODO channelWritabilityChanged
    }
}
