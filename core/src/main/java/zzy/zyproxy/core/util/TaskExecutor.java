package zzy.zyproxy.core.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/12/9
 */
public abstract class TaskExecutor {
    public static TaskExecutor createExecuter() {
        return new TaskExecutor() {
        };
    }


    private final static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public void executeTask(final Runnable runnable) {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        cachedThreadPool.execute(task);
    }
}
