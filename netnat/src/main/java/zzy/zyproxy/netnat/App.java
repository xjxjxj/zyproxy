package zzy.zyproxy.netnat;

import zzy.zyproxy.netnat.nat.NatBTPChannelClient;
import zzy.zyproxy.netnat.net.AcceptBTPServer;
import zzy.zyproxy.netnat.net.AcceptUserServer;
import zzy.zyproxy.netnat.util.ProxyConfig;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        ProxyConfig proxyConfig = new ProxyConfig();
        Map<InetSocketAddress, InetSocketAddress> user2real = new HashMap<InetSocketAddress, InetSocketAddress>();
        user2real.put(new InetSocketAddress("127.0.0.1", 3307), new InetSocketAddress("127.0.0.1", 3306));
        InetSocketAddress aceptBTPchannel = new InetSocketAddress("127.0.0.1", 8858);
        proxyConfig.setAcceptUserToRealAddrMap(user2real);
        proxyConfig.setAcceptBTPAddr(aceptBTPchannel);

        App app = new App();
        app.startAcetpServer(proxyConfig);

    }

    private void startAcetpServer(ProxyConfig proxyConfig) {
        Map<InetSocketAddress, InetSocketAddress> acceptUserToRealAddrMap = proxyConfig.getAcceptUserToRealAddrMap();
        for (final Map.Entry<InetSocketAddress, InetSocketAddress> entry : acceptUserToRealAddrMap.entrySet()) {
            new Thread() {
                @Override
                public void run() {
                    AcceptUserServer acceptUserServer = new AcceptUserServer(entry.getKey());
                    acceptUserServer.start();
                }
            }.start();
        }
        final InetSocketAddress acceptBTPchannelAddr = proxyConfig.getAcceptBTPAddr();
        new Thread() {
            @Override
            public void run() {
                new AcceptBTPServer(acceptBTPchannelAddr);
            }
        }.start();
    }

    private void startClient(final ProxyConfig proxyConfig) {
        Map<InetSocketAddress, InetSocketAddress> acceptUserToRealAddrMap = proxyConfig.getAcceptUserToRealAddrMap();
        for (final Map.Entry<InetSocketAddress, InetSocketAddress> entry : acceptUserToRealAddrMap.entrySet()) {
            new Thread() {
                @Override
                public void run() {
                    NatBTPChannelClient natBTPClient = new NatBTPChannelClient(
                        proxyConfig.getAcceptBTPAddr(),
                        entry.getValue()
                    );
                    natBTPClient.start();
                }
            }.start();
        }
    }
}
