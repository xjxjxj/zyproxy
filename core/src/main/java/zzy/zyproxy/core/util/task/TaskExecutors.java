package zzy.zyproxy.core.util.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public class TaskExecutors {

    private final Map<Integer, TaskExecutor> mapQueue = new HashMap<Integer, TaskExecutor>();

    public TaskExecutor createExclusiveSingleThreadExecuter() {
        return new ExclusiveTaskExecutor(new LinkedBlockingDeque<Runnable>());
    }

    public TaskExecutor createShareSingleThreadExecuter(int id) {
        TaskExecutor taskExecutor0 = getShareTaskExector(id);
        if (taskExecutor0 == null) {
            taskExecutor0 = new ShareTaskExecutor(id, new LinkedBlockingDeque<Runnable>());
            mapQueue.put(id, taskExecutor0);
            System.out.println("mapQueue.size()" + mapQueue.size());
        }
        return taskExecutor0;
    }

    public TaskExecutor removeShareExecuter(int userCode) {
        return mapQueue.remove(userCode);
    }

    public TaskExecutor getShareTaskExector(int userCode) {
        return mapQueue.get(userCode);
    }

    public Integer[] getShareTaskExectorUserCodes() {
        Set<Integer> keySet = mapQueue.keySet();
        return keySet.toArray(new Integer[keySet.size()]);
    }
}
