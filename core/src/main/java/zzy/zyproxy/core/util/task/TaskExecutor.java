package zzy.zyproxy.core.util.task;

/**
 * @author zhouzhongyuan
 * @date 2016/12/14
 */
public interface TaskExecutor {
    void addLast(Task task);

    void addFirst(Task task);

    TaskExecutor start();
}
