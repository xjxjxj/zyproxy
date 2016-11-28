package zzy.zyproxy.core.packet.heart;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.msgpack.annotation.Message;
import zzy.zyproxy.core.packet.Packet;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
@Message
public class HeartMsg implements Packet {
    private int HEART_TYPE = 0x000a;

    private abstract class HeartBody {
        protected abstract void setOuterHeartType();
    }

    public HeartMsg setHeartBody(HeartBody heartBody) {
        heartBody.setOuterHeartType();
        return this;
    }

    //############[================]############//
    //############[注册新的代理端口]############//
    //############[================]############//
    private static final int NAT_REGISTER_HEART = 0x001a;

    private Integer netUserAcptPort;

    public class NatRegisterHeart extends HeartBody {

        public Integer getNetAcptUserPort() {
            return netUserAcptPort;
        }

        public NatRegisterHeart setNetAcptUserPort(Integer port) {
            netUserAcptPort = port;
            return this;
        }

        protected void setOuterHeartType() {
            HEART_TYPE = NAT_REGISTER_HEART;
        }
    }

    public boolean isNatRegisterHeart() {
        return NAT_REGISTER_HEART == HEART_TYPE;
    }

    public NatRegisterHeart asSubNatRegisterHeart() {
        return new NatRegisterHeart();
    }


    //############[========== ]############//
    //############[NAT发送PING]############//
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
    private static final int NET_REQUEST_BTP_CHANNEL = 0x004a;

    private int netRequestBTPChannelNum = 1;

    public class NetRequestBTPChannel extends HeartBody {
        public void setNetRequestNewChannelNum(int num) {
            netRequestBTPChannelNum = num;
        }

        public int getNetRequestNewChannelNum() {
            return netRequestBTPChannelNum;
        }

        protected void setOuterHeartType() {
            HEART_TYPE = NET_REQUEST_BTP_CHANNEL;
        }
    }

    public boolean isNetRequestBTPChannel() {
        return NET_REQUEST_BTP_CHANNEL == HEART_TYPE;
    }

    public NetRequestBTPChannel asSubNetRequestBTPChannel() {
        return new NetRequestBTPChannel();
    }

    //############[========== ]############//
    //############[Nat端响应btp连接请求]############//
    //############[===========]############//
    private static final int NAT_RESPONSE_BTP_CHANNEL = 0x005a;

    public class NatResponseBTPChannel extends HeartBody {
        protected void setOuterHeartType() {
            HEART_TYPE = NAT_RESPONSE_BTP_CHANNEL;
        }
    }

    public boolean isNatResponseBTPChannel() {
        return NAT_RESPONSE_BTP_CHANNEL == HEART_TYPE;
    }

    public NatResponseBTPChannel asSubNatResponseBTPChannel() {
        return new NatResponseBTPChannel();
    }

    //############[========== ]############//
    //############[Nat端注册btp channel]############//
    //############[===========]############//
    private static final int NAT_REGISTER_BTP_CHANNEL = 0x006a;

    private int registerBTPAcptUserPort;

    public class NatRegisterBTPChannel extends HeartBody {

        protected void setOuterHeartType() {
            HEART_TYPE = NAT_REGISTER_BTP_CHANNEL;
        }

        public NatRegisterBTPChannel setAcptUserPort(int acptUserPort) {
            registerBTPAcptUserPort = acptUserPort;
            return this;
        }

        public int getAcptUserPort() {
            return registerBTPAcptUserPort;
        }
    }

    public boolean isNatRegisterBTPChannel() {
        return NAT_REGISTER_BTP_CHANNEL == HEART_TYPE;
    }

    public NatRegisterBTPChannel asSubNatRegisterBTPChannel() {
        return new NatRegisterBTPChannel();
    }

    //############[========== ]############//
    //############[用户发送信息传送到后端服务]############//
    //############[===========]############//
    private static final int USER_WRITE_TO_NAT_BTP = 0x007a;

    private byte[] userWriteToNatMsgBody;

    public class UserWriteToNatBTP extends HeartBody {

        protected void setOuterHeartType() {
            HEART_TYPE = USER_WRITE_TO_NAT_BTP;
        }

        public UserWriteToNatBTP setMsgBody(byte[] msgBody) {
            userWriteToNatMsgBody = msgBody;
            return this;
        }

        public byte[] getMsgBody() {
            return userWriteToNatMsgBody;
        }
    }

    public boolean isUserWriteToNatBTP() {
        return USER_WRITE_TO_NAT_BTP == HEART_TYPE;
    }

    public UserWriteToNatBTP asSubUserWriteToNatBTP() {
        return new UserWriteToNatBTP();
    }

    //############[========== ]############//
    //############[用户发送信息传送到后端服务]############//
    //############[===========]############//
    private static final int USER_CHANNEL_CONNECTED = 0x008a;

    public class UserChannelConnected extends HeartBody {
        protected void setOuterHeartType() {
            HEART_TYPE = USER_CHANNEL_CONNECTED;
        }
    }

    public boolean isUserChannelConnected() {
        return USER_CHANNEL_CONNECTED == HEART_TYPE;
    }

    public UserChannelConnected asSubUserChannelConnected() {
        return new UserChannelConnected();
    }

    //############[========== ]############//
    //############[用户发送信息传送到后端服务]############//
    //############[===========]############//
    private static final int REAL_CHANNEL_CONNECTED = 0x009a;

    public class RealChannelConnected extends HeartBody {
        protected void setOuterHeartType() {
            HEART_TYPE = REAL_CHANNEL_CONNECTED;
        }
    }

    public boolean isRealChannelConnected() {
        return REAL_CHANNEL_CONNECTED == HEART_TYPE;
    }

    public RealChannelConnected asSubRealChannelConnected() {
        return new RealChannelConnected();
    }

    ////-------------------
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("HEART_TYPE", HEART_TYPE)
            .append("netUserAcptPort", netUserAcptPort)
            .toString();
    }
}
