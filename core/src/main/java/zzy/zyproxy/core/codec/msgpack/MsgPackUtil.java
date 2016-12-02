package zzy.zyproxy.core.codec.msgpack;


import org.msgpack.MessagePack;
import org.msgpack.template.OrdinalEnumTemplate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhouzhongyuan
 * @date 2016/11/18
 */
public abstract class MsgPackUtil {
    private static MsgPackUtil instance = new MsgPackUtil() {
    };

    private Set<Class> messagePackClazzs = new HashSet<Class>();
    private MessagePack msgpacker = new MessagePack();


    private MsgPackUtil() {
    }

    @SuppressWarnings("unchecked")
    public void register0(Class objClazz) {
        if (!messagePackClazzs.contains(objClazz)) {
            if (objClazz.getEnumConstants() != null) {
                OrdinalEnumTemplate ordinalEnumTemplate = new OrdinalEnumTemplate(objClazz);
                msgpacker.register(objClazz, ordinalEnumTemplate);
            } else {
                msgpacker.register(objClazz);
            }
            messagePackClazzs.add(objClazz);
        }
    }

    public static MessagePack getMsgpacker() {
        return instance.msgpacker;
    }
}