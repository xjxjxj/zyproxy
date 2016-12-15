package zzy.zyproxy.netnat.nat;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.util.ShareChannels;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public class NatShareChannels implements ShareChannels {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatShareChannels.class);

    private ChannelHandlerContext tcpBtp;
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

    @Override
    public Integer[] getTcpUsers() {
        Set<Integer> keySet = tcpUserMap.keySet();
        return keySet.toArray(new Integer[keySet.size()]);
    }
}
