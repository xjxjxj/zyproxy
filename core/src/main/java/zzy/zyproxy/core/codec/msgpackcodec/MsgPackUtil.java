package zzy.zyproxy.core.codec.msgpackcodec;

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


    private byte[] write(Object object) throws IOException {
//        register(object.getClass());

        return msgpacker.write(object);
    }

    private void register(Class<?> objectClass) {

    }

    public <T> T read(byte[] raw, T v) throws IOException {
//        register(v.getClass());

        return msgpacker.read(raw, v);
    }

    public static byte[] pack(Object object) {
        try {
            return instance.write(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T unpack(byte[] bytes, T v) {
        try {
            return instance.read(bytes, v);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
