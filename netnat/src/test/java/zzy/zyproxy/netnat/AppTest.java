package zzy.zyproxy.netnat;

import org.junit.Test;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.*;

/**
 * @author zhouzhongyuan
 * @date 2016/12/14
 */
public class AppTest {
    @Test
    public void name0() throws Exception {
        Deque<Runnable> runnables = new LinkedBlockingDeque<Runnable>();
        for (int i = 0; i < 10000; i++) {
            runnables.addLast(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10000; i++) {
            executorService.execute(runnables.poll());
        }
    }
}