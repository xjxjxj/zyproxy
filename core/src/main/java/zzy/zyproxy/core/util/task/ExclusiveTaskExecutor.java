package zzy.zyproxy.core.util.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhouzhongyuan
 * @date 2016/12/9
 */
public class ExclusiveTaskExecutor implements TaskExecutor {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShareTaskExecutor.class);

    ///==========
    private final int id;
    private final Deque<Runnable> taskerQeque;
    private final ExecutorService executorService;
    private AtomicBoolean started = new AtomicBoolean(false);
    private AtomicBoolean threadRunning = new AtomicBoolean(false);

    protected ExclusiveTaskExecutor(int id, Deque<Runnable> taskerQeque) {
        this.id = id;
        this.taskerQeque = taskerQeque;
        executorService = Executors.newSingleThreadExecutor();
    }

    public int id() {
        return id;
    }


    public void addLast(Task task) {
        taskerQeque.addLast(t2r(task));
        if (started.get() && !threadRunning.get()) {
            Runnable runnable = taskerQeque.pollFirst();
            if (runnable != null) {
                executorService.execute(runnable);
            }
        }
    }

    public void addFirst(Task task) {
        taskerQeque.addFirst(t2r(task));
        if (started.get() && !threadRunning.get()) {
            Runnable runnable = taskerQeque.pollFirst();
            if (runnable != null) {
                executorService.execute(runnable);
            }
        }
    }

    public ExclusiveTaskExecutor start() {
        if (started.get()) {
            return this;
        }
        started.set(true);
        Runnable runnable = taskerQeque.pollFirst();
        if (runnable != null && !threadRunning.get()) {
            executorService.execute(runnable);
        }
        return this;
    }

    private Runnable t2r(final Task task) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    threadRunning.set(true);
                    LOGGER.debug("t2r run ExclusiveTaskExecutor");

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
