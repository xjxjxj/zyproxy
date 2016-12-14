package zzy.zyproxy.core.util.task;

import org.junit.Test;

/**
 * @author zhouzhongyuan
 * @date 2016/12/14
 */
public class TaskExecutorsTest {
    private TaskExecutor nihao(TaskExecutors taskExecutors, final int id) {
        TaskExecutor shareSingleThreadExecuter = taskExecutors.createShareSingleThreadExecuter(id).start();
        for (int i = 0; i < 1000; i++) {
            final int finalI = i;
            shareSingleThreadExecuter.addLast(new Task() {
                @Override
                public void run() {
                    System.out.println("{" + id + "}" + finalI + "[]" + Thread.currentThread());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return shareSingleThreadExecuter;
    }

    @Test
    public void createShareSingleThreadExecuter() throws Exception {
        final TaskExecutors taskExecutors = new TaskExecutors();

        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            new Thread(){
                @Override
                public void run() {
                    nihao(taskExecutors, finalI);
                }
            }.start();
        }


        final Object o = new Object();
        synchronized (o) {
            o.wait();
        }
    }


}