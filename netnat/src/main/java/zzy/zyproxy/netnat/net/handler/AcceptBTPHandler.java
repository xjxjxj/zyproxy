package zzy.zyproxy.netnat.net.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.BTPChannel;
import zzy.zyproxy.core.channel.NaturalChannel;
import zzy.zyproxy.core.handler.BTPInboundHandler;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.SharaChannels;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public class AcceptBTPHandler extends BTPInboundHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptBTPHandler.class);

    public AcceptBTPHandler(BTPChannel btpChannel, SharaChannels sharaChannels) {
        super(btpChannel, sharaChannels);
    }

    protected void channelReadAuth(BTPChannel btpChannel, ProxyPacket.Auth msg) {
        SharaChannels sharaChannels = sharaChannels();
        sharaChannels.addTcpBtpChannelMap(msg.getAuthCode(), btpChannel);
    }

    protected void channelReadConnected(BTPChannel btpChannel, ProxyPacket.Connected msg) {
        NaturalChannel naturalChannel = btpChannel.getNaturalChannel(msg.getUserCode());
        naturalChannel.realConnected();
    }

    protected void channelReadTransmit(BTPChannel btpChannel, ProxyPacket.Transmit msg) {
        NaturalChannel naturalChannel =
            btpChannel.getNaturalChannel(msg.getUserCode());
        naturalChannel.flushBTPChannel(btpChannel);
        naturalChannel.writeMsgAndFlush(msg.getBody());
    }

    protected void channelReadClose(BTPChannel btpChannel, ProxyPacket.Close msg) {
        NaturalChannel naturalChannel =
            btpChannel.getNaturalChannel(msg.getUserCode());
        naturalChannel.flushBTPChannel(btpChannel);
        naturalChannel.closeChannel();
    }
}
