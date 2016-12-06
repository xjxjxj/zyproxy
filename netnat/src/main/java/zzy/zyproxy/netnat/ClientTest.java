package zzy.zyproxy.netnat;

import zzy.zyproxy.netnat.util.ProxyConfig;

import java.io.IOException;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public class ClientTest {

    public static void main(String[] args) throws InterruptedException {
        ProxyConfig proxyConfig = App.proxyConfig;
        App app = new App();
        app.startClient(proxyConfig);
        System.out.println("press ENTER to call System.exit() and run the shutdown routine.");
        try {
            int read = System.in.read();
            System.out.println(read);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
