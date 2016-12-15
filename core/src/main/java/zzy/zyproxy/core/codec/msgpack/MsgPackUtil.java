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

    private MessagePack msgpacker = new MessagePack();


    private MsgPackUtil() {
    }

    public static MessagePack getMsgpacker() {
        return instance.msgpacker;
    }
}