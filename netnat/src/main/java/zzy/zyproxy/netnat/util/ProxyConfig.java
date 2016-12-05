package zzy.zyproxy.netnat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public class ProxyConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyConfig.class);

    private Map<InetSocketAddress, InetSocketAddress> acceptUserToRealAddrMap;
    private InetSocketAddress acceptBTPAddr;

    public Map<InetSocketAddress, InetSocketAddress> getAcceptUserToRealAddrMap() {
        return acceptUserToRealAddrMap;
    }

    public ProxyConfig setAcceptUserToRealAddrMap(Map<InetSocketAddress, InetSocketAddress> acceptUserToRealAddrMap) {
        this.acceptUserToRealAddrMap = acceptUserToRealAddrMap;
        return this;
    }

    public InetSocketAddress getAcceptBTPAddr() {
        return acceptBTPAddr;
    }

    public ProxyConfig setAcceptBTPAddr(InetSocketAddress acceptBTPAddr) {
        this.acceptBTPAddr = acceptBTPAddr;
        return this;
    }
}
