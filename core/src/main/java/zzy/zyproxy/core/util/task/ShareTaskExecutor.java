package zzy.zyproxy.core.util.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhouzhongyuan
 * @date 2016/12/9
 */
public class ShareTaskExecutor implements TaskExecutor {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShareTaskExecutor.class);

    private final static BlockingQueue<ShareTaskExecutor> taskExecutorQueue = new LinkedBlockingQueue<ShareTaskExecutor>();

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    static {
        for (int i = 0; i < 100; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    taskExecutorRun();
                }
            });
        }
    }

    @SuppressWarnings("InfiniteRecursion")
    private static void taskExecutorRun() {
        try {
            System.out.println(Thread.currentThread() + "【开始】");
            ShareTaskExecutor taskExecutor = taskExecutorQueue.take();
            System.out.println(Thread.currentThread() + "【结束】");

            Runnable tasker = taskExecutor.taskerQeque.pollFirst();
            if (tasker != null) {
                tasker.run();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            taskExecutorRun();
        }
    }

    ///==========
    private final int id;
    private final Deque<Runnable> taskerQeque;
    private AtomicBoolean started = new AtomicBoolean(false);
    private AtomicBoolean threadRunning = new AtomicBoolean(false);

    protected ShareTaskExecutor(int id, Deque<Runnable> taskerQeque) {
        this.id = id;
        this.taskerQeque = taskerQeque;
    }

    public int id() {
        return id;
    }


    public void addLast(Task task) {
        taskerQeque.addLast(t2r(task));
        if (started.get() && !threadRunning.get()) {
            taskExecutorQueue.add(this);
        }
    }

    public void addFirst(Task task) {
        taskerQeque.addFirst(t2r(task));
        if (started.get() && !threadRunning.get()) {
            taskExecutorQueue.add(this);
        }
    }

    public ShareTaskExecutor start() {
        if (started.compareAndSet(false, true)) {
            taskExecutorQueue.add(this);
        }
        return this;
    }

    private Runnable t2r(final Task task) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    threadRunning.set(true);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Runnable own = taskerQeque.pollFirst();
                    if (own != null) {
                        own.run();
                    } else {
                        threadRunning.set(false);
                        taskExecutorRun();
                    }
                }
            }
        };
    }
}
