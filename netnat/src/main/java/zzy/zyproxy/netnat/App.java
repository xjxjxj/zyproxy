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
    }

    public static void main(String[] args) throws InterruptedException {
        App app = new App();
        app.startAcetpBTPServer(proxyConfig);
//        app.startAcetpServer(proxyConfig);
//        Thread.sleep(2000);
//        app.startClient(proxyConfig);
        synchronized (App.class) {
            try {
                App.class.wait();
            } catch (Exception ignored) {
            }
        }

    }

    public void startAcetpBTPServer(ProxyConfig proxyConfig) {
        final NatSharaChannels natSharaChannels = new NatSharaChannels();
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

    public void startAcetpServer(ProxyConfig proxyConfig) {
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
    }

    public void startClient(final ProxyConfig proxyConfig) {
        Map<InetSocketAddress, InetSocketAddress> acceptUserToRealAddrMap = proxyConfig.getAcceptUserToRealAddrMap();
        for (final Map.Entry<InetSocketAddress, InetSocketAddress> entry : acceptUserToRealAddrMap.entrySet()) {
            new Thread() {
                @Override
                public void run() {
                    NatChannelClient natBTPClient = new NatChannelClient(
                        proxyConfig.getAcceptBTPAddr(),
                        entry.getValue()
                    );
                    natBTPClient.start();
                }
            }.start();
        }
    }
}
