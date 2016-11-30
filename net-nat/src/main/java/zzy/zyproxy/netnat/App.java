package zzy.zyproxy.netnat;

import zzy.zyproxy.netnat.natsrv.NatBTPClientServer;
import zzy.zyproxy.netnat.netsrv.AcceptNatBTPServer;
import zzy.zyproxy.netnat.netsrv.AcceptUserServer;
import zzy.zyproxy.netnat.netsrv.ChannelShare;

import java.net.InetSocketAddress;

/**
 * Hello world!
 */
public class App {
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws InterruptedException {
        final InetSocketAddress acptBTPAddr = new InetSocketAddress("127.0.0.1", 8856);

        final InetSocketAddress acptUserAddr = new InetSocketAddress("127.0.0.1", 8009);
        final InetSocketAddress natRealAddr = new InetSocketAddress("127.0.0.1", 8888);

        final InetSocketAddress acptUserAddr1 = new InetSocketAddress("127.0.0.1", 3307);
        final InetSocketAddress natRealAddr1 = new InetSocketAddress("127.0.0.1", 3306);

        final ChannelShare channelShare = new ChannelShare();
        final int allIdleTimeSeconds = 100 * 60;


        Thread acceptBTPServer = new Thread(new Runnable() {
            public void run() {
                AcceptNatBTPServer acceptNatBTPServer = new AcceptNatBTPServer(
                    acptBTPAddr,
                    channelShare,
                    allIdleTimeSeconds);
                acceptNatBTPServer.start();
            }
        });
        Thread acceptUserServer = new Thread(new Runnable() {
            public void run() {
                AcceptUserServer acceptUserServer = new AcceptUserServer(
                    acptUserAddr,
                    channelShare);
                acceptUserServer.start();
            }
        });
        Thread acceptUserServer1 = new Thread(new Runnable() {
            public void run() {
                AcceptUserServer acceptUserServer = new AcceptUserServer(
                    acptUserAddr1,
                    channelShare);
                acceptUserServer.start();
            }
        });
        //natBTPClient
        Thread natBTPClient = new Thread(new Runnable() {
            public void run() {
                NatBTPClientServer natBTPClientServer = new NatBTPClientServer(
                    acptUserAddr,
                    acptBTPAddr,
                    natRealAddr,
                    (int) (allIdleTimeSeconds * 0.8f));
                natBTPClientServer.start(1);
            }
        });
        Thread natBTPClient1 = new Thread(new Runnable() {
            public void run() {
                NatBTPClientServer natBTPClientServer = new NatBTPClientServer(
                    acptUserAddr1,
                    acptBTPAddr,
                    natRealAddr1,
                    (int) (allIdleTimeSeconds * 0.8f));
                natBTPClientServer.start(1);
            }
        });

        acceptBTPServer.start();
        acceptUserServer.start();
        acceptUserServer1.start();
        Thread.sleep(1000);
        natBTPClient.start();
        natBTPClient1.start();
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
