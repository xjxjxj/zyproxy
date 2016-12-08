package zzy.zyproxy.core.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import zzy.zyproxy.core.packet.ProxyPacket;

/**
 * @author zhouzhongyuan
 * @date 2016/12/2
 */
public interface BTPChannel {
    ChannelFuture writeMsgAndFlush(ProxyPacket msg);

    ChannelFuture writeAuth(String authCode);

    ChannelFuture writeConnected(Integer userCode);

    ChannelFuture writeTransmit(Integer userCode, byte[] msgBody);

    ChannelFuture writeClose(Integer userCode);

    NaturalChannel getNaturalChannel(Integer userCode);

    NaturalChannel putNaturalChannel(Integer userCode, NaturalChannel naturalChannel);

    void flushChannelHandlerContext(ChannelHandlerContext ctx);

    ChannelFuture flushAndClose();
}
