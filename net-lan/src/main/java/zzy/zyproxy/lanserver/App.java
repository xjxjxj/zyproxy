package zzy.zyproxy.lanserver;

import zzy.zyproxy.lanserver.lansrv.BackHeartServerClient;
import zzy.zyproxy.lanserver.netsrv.AcceptBackServer;
import zzy.zyproxy.lanserver.netsrv.AcceptHeartServer;
import zzy.zyproxy.lanserver.netsrv.AcceptUserServer;

import java.net.InetSocketAddress;

/**
 * Hello world!
 */
public class App {
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws InterruptedException {
        final InetSocketAddress heart = new InetSocketAddress("127.0.0.1", 8855);
        final InetSocketAddress back = new InetSocketAddress("127.0.0.1", 8856);
        final InetSocketAddress proxy = new InetSocketAddress("127.0.0.1", 3307);

        Thread acceptBackServer = new Thread(new Runnable() {
            public void run() {
                AcceptBackServer acceptBackServer = new AcceptBackServer(back);
                acceptBackServer.start();
            }
        });
        Thread acceptHeartServer = new Thread(new Runnable() {
            public void run() {
                AcceptHeartServer acceptHeartServer = new AcceptHeartServer(heart);
                acceptHeartServer.start();
            }
        });
        Thread acceptUserServer = new Thread(new Runnable() {
            public void run() {
                AcceptUserServer acceptUserServer = new AcceptUserServer(proxy);
                acceptUserServer.start();
            }
        });
        //backHeartServerClient
        Thread backHeartServerClient = new Thread(new Runnable() {
            public void run() {
                BackHeartServerClient backHeartServerClient = new BackHeartServerClient(heart);
                backHeartServerClient.start();
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
