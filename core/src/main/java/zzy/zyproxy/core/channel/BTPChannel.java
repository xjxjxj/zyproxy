package zzy.zyproxy.core.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import zzy.zyproxy.core.packet.ProxyPacket;

/**
 * @author zhouzhongyuan
 * @date 2016/12/2
 */
public interface BTPChannel extends ProxyChannelHandlerContext {

    ChannelFuture writeMsgAndFlush(ProxyPacket msg);

    //==
    void submitTask(Runnable task);

    //===
    NaturalChannel getNaturalChannel(Integer userCode);

    NaturalChannel putNaturalChannel(Integer userCode, NaturalChannel naturalChannel);

    void removeNaturalChannel(Integer userCode);

    //===
    ChannelFuture writeAuth(String authCode);

    ChannelFuture writeConnected(Integer userCode);

    ChannelFuture writeTransmit(Integer userCode, byte[] msgBody);

    ChannelFuture writeClose(Integer userCode);

    //===
    void channelActive();

    void channelReadAuth(ProxyPacket.Auth auth);

    void channelReadConnected(ProxyPacket.Connected connected);

    void channelReadTransmit(ProxyPacket.Transmit transmit);

    void channelReadClose(ProxyPacket.Close close);
}
