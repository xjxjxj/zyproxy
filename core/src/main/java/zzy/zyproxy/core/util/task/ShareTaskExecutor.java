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
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    ///==========
    private final int id;
    private final BlockingDeque<Runnable> taskerQeque;
    private AtomicBoolean started = new AtomicBoolean(false);
    private AtomicBoolean threadRunning = new AtomicBoolean(false);

    protected ShareTaskExecutor(int id, BlockingDeque<Runnable> taskerQeque) {
        this.id = id;
        this.taskerQeque = taskerQeque;
    }

    public int id() {
        return id;
    }


    public void addLast(Task task) {
        taskerQeque.addLast(t2r(task));
        if (started.get()) {
            if (threadRunning.compareAndSet(false, true)) {
                Runnable runnable = taskerQeque.pollFirst();
                if (runnable != null) {
                    executorService.execute(runnable);
                }
            }
        }
    }

    public void addFirst(Task task) {
        taskerQeque.addFirst(t2r(task));
        if (started.get()) {
            if (threadRunning.compareAndSet(false, true)) {
                Runnable runnable = taskerQeque.pollFirst();
                if (runnable != null) {
                    executorService.execute(runnable);
                }
            }
        }
    }

    public ShareTaskExecutor start() {
        if (started.compareAndSet(false, true)) {
            addLast(new Task() {
                @Override
                public void run() {
                    
                }
            });
        }
        return this;
    }

    private Runnable t2r(final Task task) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Runnable own = taskerQeque.pollFirst();
                    if (own != null) {
                        own.run();
                    } else {
                        threadRunning.set(false);
                    }
                }
            }
        };
    }
}
