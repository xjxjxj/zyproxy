package zzy.zyproxy.core.util;

import zzy.zyproxy.core.channel.BTPChannel;

/**
 * @author zhouzhongyuan
 * @date 2016/12/5
 */
public interface SharaChannels {
    void addTcpBtpChannelMap(String authCode, BTPChannel btpChannel);
}
