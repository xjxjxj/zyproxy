package zzy.zyproxy.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouzhongyuan
 * @date 2016/12/1
 */
public class Balancer {
    private final static Logger LOGGER = LoggerFactory.getLogger(Balancer.class);
    AtomicInteger pos = new AtomicInteger();

    public <T> T roundRobin(HashSet<T> keySet) {
        ArrayList<T> keyList = new ArrayList<T>();
        keyList.addAll(keySet);

        synchronized (this) {
            int andIncrement = pos.getAndIncrement();
            if (andIncrement >= keySet.size()) {
                pos.set(0);
            }
            T server = keyList.get(pos.get());
            return server == null ? keySet.iterator().next() : server;
        }

    }
}
