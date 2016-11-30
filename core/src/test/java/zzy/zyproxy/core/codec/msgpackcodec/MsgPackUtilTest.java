package zzy.zyproxy.core.codec.msgpackcodec;

import org.junit.Test;
import org.msgpack.MessagePack;
import zzy.zyproxy.core.packet.heart.HeartMsg;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author zhouzhongyuan
 * @date 2016/11/26
 */
public class MsgPackUtilTest {
    @Test
    public void getMsgpacker() throws Exception {
        HeartMsg heartMsg = new HeartMsg();
        MessagePack msgpacker = MsgPackUtil.getMsgpacker();
        byte[] write = msgpacker.write(heartMsg);
        System.out.println(Arrays.toString(write));
        HeartMsg read = msgpacker.read(write, HeartMsg.class);
        System.out.println(read);
    }

    @Test
    public void test0() throws Exception {
        Object o = new Object();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("nihao", o);
        map.remove("nihao");
        System.out.println(map.get("nihao"));
        System.out.println(o);
    }
}