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
    //############[  传输数据  ]############//
    //############[===========]############//
    private Integer userChannelCode;

    private static final int TRANSFER_BODY = 0x007a;

    private byte[] transferBodyMsg;

    public class TransferBody extends HeartBody {

        protected void setOuterHeartType() {
            HEART_TYPE = TRANSFER_BODY;
        }

        public TransferBody setMsgBody(byte[] msgBody) {
            transferBodyMsg = msgBody;
            return this;
        }

        public byte[] getMsgBody() {
            return transferBodyMsg;
        }

        public HeartBody setUserCode(Integer userCode) {
            userChannelCode = userCode;
            return this;
        }
        public Integer getUserCode() {
            return userChannelCode;
        }

    }

    public boolean isTransferBody() {
        return TRANSFER_BODY == HEART_TYPE;
    }

    public TransferBody asSubTransferBody() {
        return new TransferBody();
    }

    //############[=====================]############//
    //############[用户和真实服务器连接信息]############//
    //############[=====================]############//
    private static final int CONNECTED = 0x009a;

    public class Connected extends HeartBody {
        protected void setOuterHeartType() {
            HEART_TYPE = CONNECTED;
        }

        public HeartBody setUserCode(int userChannelCode0) {
            userChannelCode = userChannelCode0;
            return this;
        }

        public Integer getUserCode() {
            return userChannelCode;
        }
    }

    public boolean isConnected() {
        return CONNECTED == HEART_TYPE;
    }

    public Connected asSubConnected() {
        return new Connected();
    }


    //############[========== ]############//
    //############[断开连接信息]############//
    //############[===========]############//
    private static final int CLOSED = 0x011a;

    public class Closed extends HeartBody {
        protected void setOuterHeartType() {
            HEART_TYPE = CLOSED;
        }
        public HeartBody setUserCode(int userChannelCode0) {
            userChannelCode = userChannelCode0;
            return this;
        }

        public Integer getUserCode() {
            return userChannelCode;
        }
    }

    public boolean isClosed() {
        return CLOSED == HEART_TYPE;
    }

    public Closed asSubClosed() {
        return new Closed();
    }

    ////-------------------
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("HEART_TYPE", HEART_TYPE)
            .toString();
    }
}
