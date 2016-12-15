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
    private final Deque<Runnable> taskerQeque;
    private final ExecutorService executorService;
    private AtomicBoolean started = new AtomicBoolean(false);
    private AtomicBoolean threadRunning = new AtomicBoolean(false);

    protected ExclusiveTaskExecutor(Deque<Runnable> taskerQeque) {
        this.taskerQeque = taskerQeque;
        executorService = Executors.newSingleThreadExecutor();
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

    public ExclusiveTaskExecutor start() {
        if (started.compareAndSet(false, true)) {
            addLast(new Task() {
                @Override
                public void run() {

                }
            });
        }
        return this;
    }

    @Override
    public void shutdownNow() {
        taskerQeque.addFirst(new Runnable() {
            @Override
            public void run() {
                
            }
        });
        executorService.shutdown();
    }

    @Override
    public void shutdown() {
        taskerQeque.addLast(new Runnable() {
            @Override
            public void run() {

            }
        });
        executorService.shutdown();
    }

    private Runnable t2r(final Task task) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                    System.out.println("task.run();");
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
