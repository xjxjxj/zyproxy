package zzy.zyproxy.netnat;

import zzy.zyproxy.netnat.nat.NatChannelClient;
import zzy.zyproxy.netnat.net.AcceptBTPServer;
import zzy.zyproxy.netnat.net.AcceptUserServer;
import zzy.zyproxy.netnat.util.NatSharaChannels;
import zzy.zyproxy.netnat.util.ProxyConfig;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static ProxyConfig proxyConfig = new ProxyConfig();

    static {
        Map<InetSocketAddress, InetSocketAddress> user2real = new HashMap<InetSocketAddress, InetSocketAddress>();
        user2real.put(new InetSocketAddress("127.0.0.1", 3307), new InetSocketAddress("127.0.0.1", 3306));
        InetSocketAddress aceptBTPchannel = new InetSocketAddress("127.0.0.1", 8858);
        proxyConfig.setAcceptUserToRealAddrMap(user2real);
        proxyConfig.setAcceptBTPAddr(aceptBTPchannel);
        proxyConfig.setAuth("nihao-3307");
    }

    public static void main(String[] args) throws InterruptedException {
        App app = new App();
        final NatSharaChannels natSharaChannels = new NatSharaChannels();

        app.startAcetpBTPServer(proxyConfig, natSharaChannels);

        app.startAcetpServer(proxyConfig, natSharaChannels);
        Thread.sleep(2000);
        app.startClient(proxyConfig);

        synchronized (App.class) {
            try {
                App.class.wait();
            } catch (Exception ignored) {
            }
        }

    }

    public void startAcetpBTPServer(ProxyConfig proxyConfig, final NatSharaChannels natSharaChannels) {
        final InetSocketAddress acceptBTPchannelAddr = proxyConfig.getAcceptBTPAddr();
        new Thread() {
            @Override
            public void run() {
                try {
                    new AcceptBTPServer(acceptBTPchannelAddr, natSharaChannels).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void startAcetpServer(ProxyConfig proxyConfig, final NatSharaChannels natSharaChannels) {
        Map<InetSocketAddress, InetSocketAddress> acceptUserToRealAddrMap = proxyConfig.getAcceptUserToRealAddrMap();
        for (final Map.Entry<InetSocketAddress, InetSocketAddress> entry : acceptUserToRealAddrMap.entrySet()) {
            final InetSocketAddress aceptUserAddr = entry.getKey();
            new Thread() {
                @Override
                public void run() {
                    AcceptUserServer acceptUserServer = new AcceptUserServer(aceptUserAddr, natSharaChannels);
                    acceptUserServer.start();
                }
            }.start();
        }
    }

    public void startClient(final ProxyConfig proxyConfig) {
        Map<InetSocketAddress, InetSocketAddress> acceptUserToRealAddrMap
            = proxyConfig.getAcceptUserToRealAddrMap();
        final String auth = proxyConfig.getAuth();
        for (final Map.Entry<InetSocketAddress, InetSocketAddress> entry : acceptUserToRealAddrMap.entrySet()) {
            final InetSocketAddress realAddr = entry.getValue();
            new Thread() {
                @Override
                public void run() {
                    NatChannelClient natBTPClient = new NatChannelClient(
                        proxyConfig.getAcceptBTPAddr(),
                        realAddr,
                        auth
                    );
                    natBTPClient.start();
                }
            }.start();
        }
    }
}
