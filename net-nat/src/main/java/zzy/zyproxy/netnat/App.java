package zzy.zyproxy.netnat;

import zzy.zyproxy.netnat.natsrv.NatHeartClient;
import zzy.zyproxy.netnat.netsrv.AcceptNatBTPServer;
import zzy.zyproxy.netnat.netsrv.AcceptHeartServer;
import zzy.zyproxy.netnat.netsrv.AcceptUserServer;
import zzy.zyproxy.netnat.netsrv.ChannelShare;

import java.net.InetSocketAddress;

/**
 * Hello world!
 */
public class App {
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws InterruptedException {
        final InetSocketAddress acptHeartAddr = new InetSocketAddress("127.0.0.1", 8855);
        final InetSocketAddress acptBackAddr = new InetSocketAddress("127.0.0.1", 8856);

        final InetSocketAddress acptUserAddr = new InetSocketAddress("127.0.0.1", 8009);
        final InetSocketAddress natRealAddr = new InetSocketAddress("127.0.0.1", 80);

        final ChannelShare channelShare = new ChannelShare();
        final int allIdleTimeSeconds = 100 * 60;


        Thread acceptBackServer = new Thread(new Runnable() {
            public void run() {
                AcceptNatBTPServer acceptNatBTPServer = new AcceptNatBTPServer(acptBackAddr, channelShare);
                acceptNatBTPServer.start();
            }
        });
        Thread acceptHeartServer = new Thread(new Runnable() {
            public void run() {
                AcceptHeartServer acceptHeartServer = new AcceptHeartServer(acptHeartAddr, channelShare, allIdleTimeSeconds);
                acceptHeartServer.start();
            }
        });
        Thread acceptUserServer = new Thread(new Runnable() {
            public void run() {
                AcceptUserServer acceptUserServer = new AcceptUserServer(acptUserAddr, channelShare);
                acceptUserServer.start();
            }
        });
        //backHeartServerClient
        Thread backHeartServerClient = new Thread(new Runnable() {
            public void run() {
                NatHeartClient natHeartClient = new NatHeartClient(
                        acptHeartAddr,
                        acptUserAddr,
                        acptBackAddr,
                        natRealAddr,
                        (int) (allIdleTimeSeconds * 0.8f));
                natHeartClient.start();
            }
        });

        acceptBackServer.start();
        acceptUserServer.start();
        acceptHeartServer.start();
        Thread.sleep(3000);
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
