package zzy.zyproxy.core.packet.heart;

import org.msgpack.annotation.Message;
import org.msgpack.annotation.OrdinalEnum;
import zzy.zyproxy.core.packet.Packet;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
@Message
public class HeartMsg implements Packet{
    private HeartType heartType;
    private HeartBody heartBody;

    interface HeartBody {
    }

    @OrdinalEnum
    public enum HeartType {
        PING,
        PONG
    }

    public Boolean assertHeartType(HeartType eqHeartType) {
        if (heartType == null) {
            return null;
        }
        return heartType.equals(eqHeartType);
    }

    @SuppressWarnings("unchecked")
    public <T> T getHeartBody(Class<T> e) {
        if (e.isAssignableFrom(HeartBody.class)) {
            return (T) heartBody;
        }
        return null;
    }

}
