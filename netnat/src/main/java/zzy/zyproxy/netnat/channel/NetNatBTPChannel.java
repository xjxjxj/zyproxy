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
        if (naturalChannel != null) {
            naturalChannel.flushAndClose();
        }
    }

    //--TaskExecutor
    private final TaskExecutor taskExecutor = TaskExecutor.createExecuter();

    protected void executeTask(final Runnable runnable) {
        taskExecutor.executeTask(runnable);
    }


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

    //--do for real channel

    public abstract void channelActive();

    public abstract void channelReadAuth(final ProxyPacket.Auth auth);

    public abstract void channelReadConnected(final ProxyPacket.Connected msg);

    protected abstract Logger getLogger();

    public void channelReadTransmit(final ProxyPacket.Transmit msg) {
        executeTask(new Runnable() {
            public void run() {
                NaturalChannel naturalChannel = getNaturalChannel(msg.getUserCode());
                if (naturalChannel == null) {
                    getLogger().error("naturalChannel == null");
                }
                naturalChannel.writeMsgAndFlush(msg.getBody());
            }
        });
    }

    public void channelReadClose(final ProxyPacket.Close msg) {
        executeTask(new Runnable() {
            public void run() {
                removeNaturalChannel(msg.getUserCode());
            }
        });
    }
}
