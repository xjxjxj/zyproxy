package zzy.zyproxy.core.packet.msgpacket;

import org.msgpack.annotation.Message;
import zzy.zyproxy.core.codec.msgpack.MsgpackPacket;
import zzy.zyproxy.core.packet.ProxyPacket;

/**
 * @author zhouzhongyuan
 * @date 2016/12/2
 */
@Message
public class MsgPacket implements ProxyPacket, MsgpackPacket {
    private int MSG_TYPE = 0x000a;
    /**
     * 信息类型
     */
    private final static int AUTH = 0x001a;//注册新的channel
    private final static int CONNECTED = 0x002a;//用户连接和连接真是服务器
    private final static int TRANSMIT = 0x003a;//用户连接和连接发送信息
    private final static int CLOSE = 0x004a;//用户连接和连接发送信息
    private final static int HEART = 0x005a;//心跳
    private final static int EXCEPTION = 0x006a;//异常
    private String authCode;
    private String exceptionMessage;
    private Integer userCode;
    private Integer[] userCodes;
    private byte[] msgBody;

    private class Action {
        Action(int msgType) {
            if (MSG_TYPE == 0x000a) {
                MSG_TYPE = msgType;
            }
        }
    }

    //===---
    public class Heart extends Action implements ProxyPacket.Heart {
        Heart(int msgType) {
            super(msgType);
        }

        @Override
        public Integer[] getUserCodes() {
            return userCodes;
        }

        @Override
        public void setUserCodes(Integer[] userCodes) {
            MsgPacket.this.userCodes = userCodes;
        }
    }

    public boolean isHeart() {
        return HEART == MSG_TYPE;
    }

    public Heart asHeart() {
        return newHeart();
    }

    public Heart newHeart() {
        return new Heart(HEART);
    }

    //===---
    public class Exception extends Action implements ProxyPacket.Exception {
        Exception(int msgType) {
            super(msgType);
        }

        public String getMessage() {
            return exceptionMessage;
        }

        public void setMessage(String message) {
            exceptionMessage = message;
        }

        public Integer getUserCode() {
            return userCode;
        }

        public void setUserCode(Integer userCode) {
            MsgPacket.this.userCode = userCode;
        }
    }

    public boolean isException() {
        return EXCEPTION == MSG_TYPE;
    }

    public Exception asException() {
        return newException();
    }

    public Exception newException() {
        return new Exception(EXCEPTION);
    }


    //===---
    public class Auth extends Action implements ProxyPacket.Auth {
        Auth(int msgType) {
            super(msgType);
        }

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            MsgPacket.this.authCode = authCode;
        }
    }

    public boolean isAuth() {
        return AUTH == MSG_TYPE;
    }

    public ProxyPacket.Auth asAuth() {
        return newAuth();
    }

    public ProxyPacket.Auth newAuth() {
        return new Auth(AUTH);
    }


    //===---
    class Connected extends Action implements ProxyPacket.Connected {
        Connected(int msgType) {
            super(msgType);
        }

        public Integer getUserCode() {
            return userCode;
        }

        public void setUserCode(Integer userCode) {
            MsgPacket.this.userCode = userCode;
        }
    }

    public boolean isConnected() {
        return CONNECTED == MSG_TYPE;
    }

    public Connected asConnected() {
        return newConnected();
    }

    public Connected newConnected() {
        return new Connected(CONNECTED);
    }


    //===---
    class Transmit extends Action implements ProxyPacket.Transmit {
        Transmit(int msgType) {
            super(msgType);
        }

        public Integer getUserCode() {
            return MsgPacket.this.userCode;
        }

        public void setUserCode(Integer userCode) {
            MsgPacket.this.userCode = userCode;
        }

        public byte[] getBody() {
            return MsgPacket.this.msgBody;
        }

        public void setBody(byte[] bytes) {
            MsgPacket.this.msgBody = bytes;
        }
    }

    public boolean isTransmit() {
        return TRANSMIT == MSG_TYPE;
    }

    public Transmit asTransmit() {
        return newTransmit();
    }

    public Transmit newTransmit() {
        return new Transmit(TRANSMIT);
    }


    //===---
    class Close extends Action implements ProxyPacket.Close {
        Close(int msgType) {
            super(msgType);
        }

        public Integer getUserCode() {
            return MsgPacket.this.userCode;
        }

        public void setUserCode(Integer userCode) {
            MsgPacket.this.userCode = userCode;
        }
    }

    public boolean isClose() {
        return CLOSE == MSG_TYPE;
    }

    public Close asClose() {
        return newClose();
    }

    public Close newClose() {
        return new Close(CLOSE);
    }
}
