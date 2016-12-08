package zzy.zyproxy.netnat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.util.SharaChannels;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public class NatSharaChannels implements SharaChannels {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatSharaChannels.class);

    private final Map<Integer, BTPChannel> tcpBtpChannelMap
        = new HashMap<Integer, BTPChannel>();

    public synchronized void putTcpBtpChannel(String authCode, BTPChannel btpChannel) {
        LOGGER.debug("putTcpBtpChannel authCode:{}, btpChannel:{}", authCode, btpChannel);
        String[] split = authCode.split("-");
        String port = null;
        String auth;
        if (split.length == 2) {
            port = split[1];
            auth = split[0];
        }
        if (port != null) {
            tcpBtpChannelMap.put(Integer.parseInt(port), btpChannel);
        }
    }

    public BTPChannel getTcpBtpChannel(Integer port) {
        return tcpBtpChannelMap.get(port);
    }
}
