package zzy.zyproxy.netnat.channel;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.channel.ProxyChannel;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.TaskExecutor;
import zzy.zyproxy.netnat.util.ProxyPacketFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhouzhongyuan
 * @date 2016/12/3
 */
public abstract class NetNatBTPChannel extends ProxyChannel implements BTPChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(NetNatBTPChannel.class);

    protected abstract Logger subLogger();

    public ChannelFuture writeMsgAndFlush(ProxyPacket msg) {
        return super.writeAndFlush(msg);
    }

    //-- naturalChannelHashMap
    private volatile Map<Integer, NaturalChannel> naturalChannelHashMap
        = new ConcurrentHashMap<Integer, NaturalChannel>();

    public NaturalChannel getNaturalChannel(Integer userCode) {
        return naturalChannelHashMap.get(userCode);
    }

    public NaturalChannel putNaturalChannel(Integer userCode, NaturalChannel naturalChannel) {
        return naturalChannelHashMap.put(userCode, naturalChannel);
    }

    public void removeNaturalChannel(Integer userCode) {
        NaturalChannel naturalChannel = naturalChannelHashMap.remove(userCode);
        if (naturalChannel != null && naturalChannel.ctxchannelIsActive()) {
            naturalChannel.flushAndClose();
        }
    }

    //--TaskExecutor
    private final TaskExecutor taskExecutor = TaskExecutor.createNonQueueExecuter();

    public void submitTask(final Runnable runnable) {
        taskExecutor.submitTask(runnable);
    }

    //==
    public ChannelFuture writeAuth(String authCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Auth auth = proxyPacket.newAuth();
        auth.setAuthCode(authCode);
        return writeMsgAndFlush(proxyPacket);
    }

    //--use from real channel
    public ChannelFuture writeConnected(Integer userCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Connected connected = proxyPacket.newConnected();
        connected.setUserCode(userCode);
        return writeMsgAndFlush(proxyPacket);
    }

    public ChannelFuture writeTransmit(Integer userCode, byte[] msgBody) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Transmit transmit = proxyPacket.newTransmit();
        transmit.setUserCode(userCode);
        transmit.setBody(msgBody);
        return writeMsgAndFlush(proxyPacket);
    }

    public ChannelFuture writeClose(Integer userCode) {
        ProxyPacket proxyPacket = ProxyPacketFactory.newProxyPacket();
        ProxyPacket.Close close = proxyPacket.newClose();
        close.setUserCode(userCode);
        return writeMsgAndFlush(proxyPacket);
    }

    //--do for natural channel
    public void channelReadAuth(final ProxyPacket.Auth msg) {
        submitTask(channelReadAuthRunnable(msg));
    }

    public abstract Runnable channelReadAuthRunnable(final ProxyPacket.Auth msg);


    public void channelReadConnected(final ProxyPacket.Connected msg) {
        submitTask(channelReadConnectedRunnable(msg));
    }

    public abstract Runnable channelReadConnectedRunnable(final ProxyPacket.Connected msg);


    public void channelReadTransmit(final ProxyPacket.Transmit msg) {
        submitTask(new Runnable() {
            public void run() {
                NaturalChannel naturalChannel = getNaturalChannel(msg.getUserCode());
                if (naturalChannel == null) {
                    subLogger().error("naturalChannel == null");
                    return;
                }
                naturalChannel.writeMsgAndFlush(msg.getBody());
            }
        });
    }

    public void channelReadClose(final ProxyPacket.Close msg) {
        submitTask(new Runnable() {
            public void run() {
                removeNaturalChannel(msg.getUserCode());
            }
        });
    }

    public void channelActive() {
        submitTask(channelActiveRunnable());
    }

    protected abstract Runnable channelActiveRunnable();
}
