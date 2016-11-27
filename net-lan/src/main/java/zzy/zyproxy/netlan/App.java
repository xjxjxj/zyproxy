package zzy.zyproxy.netlan;

import zzy.zyproxy.netlan.lansrv.LanHeartClient;
import zzy.zyproxy.netlan.netsrv.AcceptBackServer;
import zzy.zyproxy.netlan.netsrv.AcceptHeartServer;
import zzy.zyproxy.netlan.netsrv.AcceptUserServer;
import zzy.zyproxy.netlan.netsrv.ChannelShare;

import java.net.InetSocketAddress;

/**
 * Hello world!
 */
public class App {
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws InterruptedException {
        final InetSocketAddress acptHeartAddr = new InetSocketAddress("127.0.0.1", 8855);
        final InetSocketAddress acptBackAddr = new InetSocketAddress("127.0.0.1", 8856);
        final InetSocketAddress acptUserAddr = new InetSocketAddress("127.0.0.1", 3307);

        final InetSocketAddress lanRealAddr = new InetSocketAddress("127.0.0.1", 3306);
        final ChannelShare channelShare = new ChannelShare();
        final int allIdleTimeSeconds = 100 * 60;


        Thread acceptBackServer = new Thread(new Runnable() {
            public void run() {
                AcceptBackServer acceptBackServer = new AcceptBackServer(acptBackAddr, channelShare);
                acceptBackServer.start();
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
                LanHeartClient lanHeartClient = new LanHeartClient(
                        acptHeartAddr,
                        acptUserAddr,
                        acptBackAddr,
                        lanRealAddr,
                        (int) (allIdleTimeSeconds * 0.8f));
                lanHeartClient.start();
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
