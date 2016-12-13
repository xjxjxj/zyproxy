package zzy.zyproxy.core.util.task;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouzhongyuan
 * @date 2016/12/9
 */
public class TaskExecutor {
    private final static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();


    ///==========
    private final int id;
    private final Queue<Runnable> runnables;

    protected TaskExecutor(int id, Queue<Runnable> runnables) {
        this.id = id;
        this.runnables = runnables;
    }

    public int id() {
        return id;
    }

    public void submitQueueTask(Runnable runnable) {
        runnables.add(runnable);
    }

    public TaskExecutor start() {
        return this;
    }

}
