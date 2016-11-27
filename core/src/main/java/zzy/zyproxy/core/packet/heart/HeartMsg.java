package zzy.zyproxy.core.packet.heart;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.OrdinalEnum;
import zzy.zyproxy.core.packet.Packet;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
@Message
public class HeartMsg implements Packet {
    private int HEART_TYPE = 0x000a;

    abstract class HeartBody {
        protected abstract void setOuterHeartType();
    }

    public HeartMsg setHeartBody(HeartBody heartBody) {
        heartBody.setOuterHeartType();
        return this;
    }

    //############[================]############//
    //############[注册新的代理端口]############//
    //############[================]############//
    private static final int REGISTER_LAN_HEART = 0x001a;

    private Integer registerLanHeartPort;

    public class RegisterLanHeart extends HeartBody {

        public Integer getProxyPort() {
            return registerLanHeartPort;
        }

        public RegisterLanHeart setProxyPort(Integer port) {
            registerLanHeartPort = port;
            return this;
        }

        protected void setOuterHeartType() {
            HEART_TYPE = REGISTER_LAN_HEART;
        }
    }

    public boolean isRegisterLanHeart() {
        return REGISTER_LAN_HEART == HEART_TYPE;
    }

    public RegisterLanHeart asSubRegisterLanHeart() {
        return new RegisterLanHeart();
    }


    //############[========== ]############//
    //############[LAN发送PING]############//
    //############[===========]############//
    private static final int PING = 0x002a;

    public class Ping extends HeartBody {
        protected void setOuterHeartType() {
            HEART_TYPE = PING;
        }
    }

    public boolean isPing() {
        return PING == HEART_TYPE;
    }

    public Ping asSubPing() {
        return new Ping();
    }

    //############[========== ]############//
    //############[NET响应PONG]############//
    //############[===========]############//
    private static final int PONG = 0x003a;

    public class Pong extends HeartBody {
        protected void setOuterHeartType() {
            HEART_TYPE = PONG;
        }
    }

    public boolean isPong() {
        return PONG == HEART_TYPE;
    }

    public Pong asSubPong() {
        return new Pong();
    }

    //############[========== ]############//
    //############[net端发起back连接请求]############//
    //############[===========]############//
    private static final int NET_REQUEST_NEW_CHANNEL = 0x004a;

    public class NetRequestNewChannel extends HeartBody {
        protected void setOuterHeartType() {
            HEART_TYPE = NET_REQUEST_NEW_CHANNEL;
        }
    }

    public boolean isNetRequestNewChannel() {
        return NET_REQUEST_NEW_CHANNEL == HEART_TYPE;
    }

    public NetRequestNewChannel asSubNetRequestNewChannel() {
        return new NetRequestNewChannel();
    }

    //############[========== ]############//
    //############[lan端响应back连接请求]############//
    //############[===========]############//
    private static final int LAN_RESPONSE_NEW_CHANNEL = 0x005a;

    public class LanResponseNewChannel extends HeartBody {
        protected void setOuterHeartType() {
            HEART_TYPE = LAN_RESPONSE_NEW_CHANNEL;
        }
    }

    public boolean isLanResponseNewChannel() {
        return LAN_RESPONSE_NEW_CHANNEL == HEART_TYPE;
    }

    public LanResponseNewChannel asSubLanResponseNewChannel() {
        return new LanResponseNewChannel();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("HEART_TYPE", HEART_TYPE)
                .append("registerLanHeartPort", registerLanHeartPort)
                .toString();
    }
}
