package zzy.zyproxy.netnat.nat;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.util.SharaChannels;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public class NatSharaChannels implements SharaChannels {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatSharaChannels.class);

    private  ChannelHandlerContext tcpBtp;
    private final Map<Integer, ChannelHandlerContext> tcpUserMap
        = new HashMap<Integer, ChannelHandlerContext>();


    public void putTcpBtp(String authCode, ChannelHandlerContext tcpBtpCtx) {
        tcpBtp = tcpBtpCtx;
    }

    public ChannelHandlerContext getTcpBtp(Integer port) {
        return tcpBtp;
    }

    public void removeTcpBtp(ChannelHandlerContext tcpBtpCtx) {
        //TODO 
    }

    public ChannelHandlerContext getTcpUser(Integer userCode) {
        return tcpUserMap.get(userCode);
    }

    public ChannelHandlerContext putTcpUser(Integer userCode, ChannelHandlerContext tcpUserCtx, Integer port) {
        tcpUserMap.put(userCode, tcpUserCtx);
        return getTcpBtp(port);
    }

    public ChannelHandlerContext removeTcpUser(Integer userCode) {
        return tcpUserMap.remove(userCode);
    }
}
