package zzy.zyproxy.core.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouzhongyuan
 * @date 2016/12/9
 */
public abstract class TaskExecutor {
    private final static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private final static AtomicInteger idInteger = new AtomicInteger();
    private final static Map<String, Queue<Runnable>> mapQueue = new HashMap<String, Queue<Runnable>>();

    public static TaskExecutor createQueueExecuter() {
        return new TaskExecutor(
            String.valueOf(idInteger.getAndIncrement())
        ) {
        };
    }

    public static TaskExecutor createNonQueueExecuter() {
        return new TaskExecutor(
            String.valueOf(idInteger.getAndIncrement())
        ) {
        };
    }

    private final String id;
    private final Queue<Runnable> runnables = new LinkedList<Runnable>();

    public TaskExecutor(String id) {
        this.id = id;
        mapQueue.put(id, runnables);
    }

    public String id() {
        return id;
    }

    public Queue<Runnable> tasks() {
        return runnables;
    }

    public void submitTask(Runnable runnable) {
        runnables.add(runnable);
    }
}
