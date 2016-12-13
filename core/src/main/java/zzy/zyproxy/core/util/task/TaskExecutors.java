package zzy.zyproxy.core.util.task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public class TaskExecutors {
    private final static AtomicInteger idInteger = new AtomicInteger();

    private final Map<Integer, TaskExecutor> mapQueue = new HashMap<Integer, TaskExecutor>();

    public TaskExecutor createExclusiveSingleThreadExecuter() {
        return new TaskExecutor(idInteger.getAndIncrement(), new LinkedList<Runnable>()) {
        };
    }

    public TaskExecutor createSharaSingleThreadExecuter(int id) {
        TaskExecutor taskExecutor0 = mapQueue.get(id);
        if (taskExecutor0 == null) {
            TaskExecutor taskExecutor = new TaskExecutor(id, new LinkedList<Runnable>()) {
            };
            mapQueue.put(id, taskExecutor);
        }
        return taskExecutor0;
    }

    public TaskExecutor getTaskExector(int userCode) {
        return mapQueue.get(userCode);
    }
}
