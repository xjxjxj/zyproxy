package zzy.zyproxy.core.util;

/**
 * @author zhouzhongyuan
 * @date 2016/12/12
 */
public abstract class Callable<I> implements Runnable {
    public void run() {
        
    }

    public abstract void callback(I obj);
}
