package zzy.zyproxy.netnat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public class ProxyConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyConfig.class);

    private List<Proxy> proxyList = new ArrayList<Proxy>();
    private InetSocketAddress acceptBTPAddr;

    public List<Proxy> addProxy(InetSocketAddress acceptUserAddr, InetSocketAddress realAddr, String auth) {
        Proxy proxy = new Proxy();
        proxy.setAuth(auth);
        proxy.setAcceptUserAddr(acceptUserAddr);
        proxy.setRealAddr(realAddr);
        proxyList.add(proxy);
        return proxyList;
    }

    public List<Proxy> proxyList() {
        return proxyList;
    }

    public InetSocketAddress acceptBTPAddr() {
        return acceptBTPAddr;
    }

    public ProxyConfig setAcceptBTPAddr(InetSocketAddress acceptBTPAddr) {
        this.acceptBTPAddr = acceptBTPAddr;
        return this;
    }

    public class Proxy {
        private InetSocketAddress acceptUserAddr;
        private InetSocketAddress realAddr;
        private String auth;

        public InetSocketAddress getAcceptUserAddr() {
            return acceptUserAddr;
        }

        public void setAcceptUserAddr(InetSocketAddress acceptUserAddr) {
            this.acceptUserAddr = acceptUserAddr;
        }

        public InetSocketAddress getRealAddr() {
            return realAddr;
        }

        public void setRealAddr(InetSocketAddress realAddr) {
            this.realAddr = realAddr;
        }

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }
    }
}
