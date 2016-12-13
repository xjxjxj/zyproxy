package zzy.zyproxy.netnat.net;

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
public class NetSharaChannels implements SharaChannels {
    private final static Logger LOGGER = LoggerFactory.getLogger(NetSharaChannels.class);

    private final Map<Integer, ChannelHandlerContext> tcpBtpMap
        = new HashMap<Integer, ChannelHandlerContext>();
    private final Map<Integer, ChannelHandlerContext> tcpUserMap
        = new HashMap<Integer, ChannelHandlerContext>();


    public synchronized void putTcpBtp(String authCode, ChannelHandlerContext tcpBtpCtx) {
        String[] split = authCode.split("-");
        String port = null;
        String auth;
        if (split.length == 2) {
            port = split[1];
            auth = split[0];
        }
        if (port != null) {
            tcpBtpMap.put(Integer.parseInt(port), tcpBtpCtx);
        }
    }

    public ChannelHandlerContext getTcpBtp(Integer port) {
        return tcpBtpMap.get(port);
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
