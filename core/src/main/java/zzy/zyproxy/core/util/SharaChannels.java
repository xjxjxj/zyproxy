package zzy.zyproxy.core.util;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public interface SharaChannels {
    void putTcpBtp(String authCode, ChannelHandlerContext tcpBtpCtx);

    ChannelHandlerContext getTcpBtp(Integer port);

    void removeTcpBtp(ChannelHandlerContext tcpBtpCtx);

    ChannelHandlerContext getTcpUser(Integer userCode);

    /**
     * @param userCode 用户id
     * @param tcpUserCtx 用户ctx
     * @param port tcpBtp端口
     * @return tcpBtp
     */
    ChannelHandlerContext putTcpUser(Integer userCode, ChannelHandlerContext tcpUserCtx, Integer port);

    ChannelHandlerContext removeTcpUser(Integer userCode);

}
