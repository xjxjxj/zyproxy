package zzy.zyproxy.netnat;

import zzy.zyproxy.netnat.nat.NatChannelClient;
import zzy.zyproxy.netnat.net.AcceptBTPServer;
import zzy.zyproxy.netnat.net.AcceptUserServer;
import zzy.zyproxy.netnat.util.NatSharaChannels;
import zzy.zyproxy.netnat.util.ProxyConfig;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static ProxyConfig proxyConfig = new ProxyConfig();

    static {
        proxyConfig.setAcceptBTPAddr(new InetSocketAddress("127.0.0.1", 8858));
        proxyConfig.addProxy(new InetSocketAddress("127.0.0.1", 3307), new InetSocketAddress("127.0.0.1", 3306), "nihao-3307");
        proxyConfig.addProxy(new InetSocketAddress("127.0.0.1", 8081), new InetSocketAddress("127.0.0.1", 80), "nihao-8081");
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
        final InetSocketAddress acceptBTPchannelAddr = proxyConfig.acceptBTPAddr();
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
        List<ProxyConfig.Proxy> proxies = proxyConfig.proxyList();
        for (final ProxyConfig.Proxy proxy : proxies) {
            new Thread() {
                @Override
                public void run() {
                    AcceptUserServer acceptUserServer
                            = new AcceptUserServer(proxy.getAcceptUserAddr(), natSharaChannels);
                    acceptUserServer.start();
                }
            }.start();
        }
    }

    public void startClient(final ProxyConfig proxyConfig) {
        List<ProxyConfig.Proxy> proxies = proxyConfig.proxyList();
        for (final ProxyConfig.Proxy proxy : proxies) {
            new Thread() {
                @Override
                public void run() {
                    NatChannelClient natBTPClient = new NatChannelClient(
                            proxyConfig.acceptBTPAddr(),
                            proxy.getRealAddr(),
                            proxy.getAuth()
                    );
                    natBTPClient.start();
                }
            }.start();
        }
    }
}
