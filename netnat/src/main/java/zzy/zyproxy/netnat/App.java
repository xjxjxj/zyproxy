package zzy.zyproxy.netnat;

import zzy.zyproxy.core.util.task.TaskExecutors;
import zzy.zyproxy.netnat.nat.NatBTPClient;
import zzy.zyproxy.netnat.net.AcceptBTPServer;
import zzy.zyproxy.netnat.net.AcceptTcpUserServer;
import zzy.zyproxy.netnat.net.NetShareChannels;
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
        proxyConfig.addProxy(new InetSocketAddress("127.0.0.1", 8081), new InetSocketAddress("127.0.0.1", 8080), "nihao-8081");
    }

    public static void main(String[] args) throws InterruptedException {
        App app = new App();
        final NetShareChannels netShareChannels = new NetShareChannels();
        final TaskExecutors netTaskExecutors = new TaskExecutors();
        final TaskExecutors natTaskExecutors = new TaskExecutors();

        app.startAcetpBTPServer(proxyConfig, netShareChannels, netTaskExecutors);

        app.startAcetpServer(proxyConfig, netShareChannels, netTaskExecutors);
        Thread.sleep(2000);
        app.startClient(proxyConfig, natTaskExecutors);

        synchronized (App.class) {
            try {
                App.class.wait();
            } catch (Exception ignored) {
            }
        }

    }

    public void startAcetpBTPServer(ProxyConfig proxyConfig, final NetShareChannels netShareChannels, final TaskExecutors netTaskExecutors) {
        final InetSocketAddress acceptBTPchannelAddr = proxyConfig.acceptBTPAddr();
        new Thread() {
            @Override
            public void run() {
                try {
                    new AcceptBTPServer(acceptBTPchannelAddr, netShareChannels, netTaskExecutors).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void startAcetpServer(ProxyConfig proxyConfig, final NetShareChannels netShareChannels, final TaskExecutors netTaskExecutors) {
        List<ProxyConfig.Proxy> proxies = proxyConfig.proxyList();
        for (final ProxyConfig.Proxy proxy : proxies) {
            new Thread() {
                @Override
                public void run() {
                    AcceptTcpUserServer acceptTcpUserServer
                        = new AcceptTcpUserServer(proxy.getAcceptUserAddr(), netShareChannels, netTaskExecutors);
                    acceptTcpUserServer.start();
                }
            }.start();
        }
    }

    public void startClient(final ProxyConfig proxyConfig, final TaskExecutors natTaskExecutors) {
        List<ProxyConfig.Proxy> proxies = proxyConfig.proxyList();
        for (final ProxyConfig.Proxy proxy : proxies) {
            new Thread() {
                @Override
                public void run() {
                    NatBTPClient natBTPClient = new NatBTPClient(
                        proxyConfig.acceptBTPAddr(),
                        proxy.getRealAddr(),
                        proxy.getAuth(),
                        natTaskExecutors
                    );
                    natBTPClient.start();
                }
            }.start();
        }
    }
}
