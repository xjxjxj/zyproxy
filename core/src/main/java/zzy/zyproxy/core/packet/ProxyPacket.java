package zzy.zyproxy.core.packet;

/**
 * @author zhouzhongyuan
 * @date 2016/12/2
 */
public interface ProxyPacket {

    //注册新的channel
    interface Auth {
        String getAuthCode();

        void setAuthCode(String authCode);
    }

    boolean isAuth();

    Auth asAuth();

    Auth newAuth();

    //用户连接和连接真是服务器
    interface Connected {
        Integer getUserCode();

        void setUserCode(Integer userCode);
    }

    boolean isConnected();

    Connected asConnected();

    Connected newConnected();

    //用户连接和连接发送信息
    interface Transmit {
        Integer getUserCode();

        void setUserCode(Integer userCode);

        byte[] getBody();

        void setBody(byte[] bytes);
    }

    boolean isTransmit();

    Transmit asTransmit();

    Transmit newTransmit();

    //用户关闭信息
    interface Close {
        Integer getUserCode();

        void setUserCode(Integer userCode);
    }

    boolean isClose();

    Close asClose();

    Close newClose();

}
