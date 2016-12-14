package zzy.zyproxy.core.util.task;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public class TaskExecutors {
    private final static AtomicInteger idInteger = new AtomicInteger();

    private final Map<Integer, TaskExecutor> mapQueue = new ConcurrentHashMap<Integer, TaskExecutor>();

    public TaskExecutor createExclusiveSingleThreadExecuter() {
        return new ExclusiveTaskExecutor(idInteger.getAndIncrement(), new LinkedList<Runnable>());
    }

    public TaskExecutor createShareSingleThreadExecuter(int id) {
        TaskExecutor taskExecutor0 = getTaskExector(id);
        if (taskExecutor0 == null) {
            taskExecutor0 = new ShareTaskExecutor(id, new LinkedList<Runnable>());
            mapQueue.put(id, taskExecutor0);
        }
        return taskExecutor0;
    }

    public TaskExecutor getTaskExector(int userCode) {
        return mapQueue.get(userCode);
    }
}
