package zzy.zyproxy.core.channel;

import zzy.zyproxy.core.packet.Packet;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public abstract class UserToBackChannel {
    UserToFrontProxyChannel userToFrontProxyChannel;
    FrontToBackProxyChannel frontToBackProxyChannel;

    public UserToBackChannel(UserToFrontProxyChannel userToFrontProxyChannel, FrontToBackProxyChannel frontToBackProxyChannel) {
        this.userToFrontProxyChannel = userToFrontProxyChannel;
        this.frontToBackProxyChannel = frontToBackProxyChannel;
    }

    UserToFrontProxyChannel getUserToFrontProxyChannel() {
        return userToFrontProxyChannel;
    }

    FrontToBackProxyChannel getFrontToBackProxyChannel() {
        return frontToBackProxyChannel;
    }

    abstract void sendMsg(Packet packet);
}
