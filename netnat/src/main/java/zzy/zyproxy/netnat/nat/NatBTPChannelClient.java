package zzy.zyproxy.netnat.nat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.server.BTPChannelClient;

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public class NatBTPChannelClient extends BTPChannelClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(NatBTPChannelClient.class);
    private final InetSocketAddress acceptBTPAddr;
    private final InetSocketAddress realAddr;

    public NatBTPChannelClient(InetSocketAddress acceptBTPAddr, InetSocketAddress realAddr) {

        this.acceptBTPAddr = acceptBTPAddr;
        this.realAddr = realAddr;
    }

    public void start() {
        
    }
}
