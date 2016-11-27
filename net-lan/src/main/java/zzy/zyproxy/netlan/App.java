package zzy.zyproxy.netlan;

import zzy.zyproxy.netlan.lansrv.LanHeartServerClient;
import zzy.zyproxy.netlan.netsrv.AcceptBackServer;
import zzy.zyproxy.netlan.netsrv.AcceptHeartServer;
import zzy.zyproxy.netlan.netsrv.AcceptUserServer;
import zzy.zyproxy.netlan.netsrv.BackChannelPool;

import java.net.InetSocketAddress;

/**
 * Hello world!
 */
public class App {
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws InterruptedException {
        final InetSocketAddress heartAddr = new InetSocketAddress("127.0.0.1", 8855);
        final InetSocketAddress backAddr = new InetSocketAddress("127.0.0.1", 8856);
        final InetSocketAddress lanProxyAddr = new InetSocketAddress("127.0.0.1", 3307);
        final InetSocketAddress netProxyAddr = new InetSocketAddress("127.0.0.1", 3307);
        final BackChannelPool backChannelPool = new BackChannelPool();
        final int allIdleTimeSeconds = 100 * 60;


        Thread acceptBackServer = new Thread(new Runnable() {
            public void run() {
                AcceptBackServer acceptBackServer = new AcceptBackServer(backAddr, backChannelPool);
                acceptBackServer.start();
            }
        });
        Thread acceptHeartServer = new Thread(new Runnable() {
            public void run() {
                AcceptHeartServer acceptHeartServer = new AcceptHeartServer(heartAddr, backChannelPool, allIdleTimeSeconds);
                acceptHeartServer.start();
            }
        });
        Thread acceptUserServer = new Thread(new Runnable() {
            public void run() {
                AcceptUserServer acceptUserServer = new AcceptUserServer(netProxyAddr, backChannelPool);
                acceptUserServer.start();
            }
        });
        //backHeartServerClient
        Thread backHeartServerClient = new Thread(new Runnable() {
            public void run() {
                LanHeartServerClient lanHeartServerClient = new LanHeartServerClient(
                        heartAddr,
                        lanProxyAddr,
                        (int) (allIdleTimeSeconds * 0.8f));
                lanHeartServerClient.start();
            }
        });

        acceptBackServer.start();
        acceptUserServer.start();
        acceptHeartServer.start();
        Thread.sleep(5000);
        backHeartServerClient.start();
        synchronized (App.class) {
            do {
                try {
                    App.class.wait();
                } catch (InterruptedException ignored) {
                }
            } while (true);
        }
    }
}
