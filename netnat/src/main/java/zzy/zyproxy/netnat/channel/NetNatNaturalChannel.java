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
import zzy.zyproxy.netnat.nat.channel.NatBTPChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public abstract class NetNatNaturalChannel extends ProxyChannel implements NaturalChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NetNatNaturalChannel.class);

    public NetNatNaturalChannel() {
        super();
    }

    public ChannelFuture writeMsgAndFlush(byte[] body) {
        return super.writeAndFlush(Unpooled.wrappedBuffer(body));
    }

    //--
    private Integer userCode = null;
    private final Object userCodeNullLocker = new Object();

    public Integer userCode() {
        if (userCode == null) {
            synchronized (userCodeNullLocker) {
                try {
                    userCodeNullLocker.wait(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return userCode;
    }

    protected void setUserCode(Integer userCode) {
        synchronized (userCodeNullLocker) {
            this.userCode = userCode;
            userCodeNullLocker.notifyAll();
        }
    }

    //---
    private final TaskExecutor taskExecutor = TaskExecutor.createExecuter();

    protected void executeTask(final Runnable runnable) {
        taskExecutor.executeTask(runnable);
    }

    //--
    private volatile Runnable connectedEvent;

    public void regConnectedEvent(final Runnable connectedEvent) {
        this.connectedEvent = new Runnable() {
            public void run() {
                connectedEvent.run();
                NetNatNaturalChannel.this.connectedEvent = null;
            }
        };
    }

    public void triggerConnectedEvent() {
        if (connectedEvent != null) {
            executeTask(connectedEvent);
        }
    }

    //==
    private BTPChannel btpChannel = null;
    private final Object btpChannelNullLocker = new Object();

    public BTPChannel btpChannel() {
        if (btpChannel == null) {
            synchronized (btpChannelNullLocker) {
                try {
                    btpChannelNullLocker.wait(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return btpChannel;
    }

    protected void setBTPChannel(BTPChannel btpChannel) {
        synchronized (btpChannelNullLocker) {
            this.btpChannel = btpChannel;
            btpChannelNullLocker.notifyAll();
        }
    }

    public abstract void channelActive();

    public void channelRead(final byte[] bytes) {
        executeTask(new Runnable() {
            public void run() {
                btpChannel()
                    .writeTransmit(userCode(), bytes)
                    .addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isSuccess()) {
                                ctxRead();
                            }
                        }
                    });
            }
        });
    }

    public void channelInactive() {
        executeTask(new Runnable() {
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
