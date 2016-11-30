package zzy.zyproxy.netnat.natsrv.channel;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.ProxyChannel;
import zzy.zyproxy.core.packet.heart.HeartMsg;

import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class RealNatBTPChannel extends ProxyChannel<RealNatBTPChannel, HeartMsg> {
    private final static Logger LOGGER = LoggerFactory.getLogger(RealNatBTPChannel.class);

    private HashMap<Integer, RealChannel> realChannelMap = new HashMap<Integer, RealChannel>();

    public RealNatBTPChannel(Channel channel) {
        super(channel);
    }


    public void close() {
        LOGGER.error("RealNatBTPChannel#close!!");
    }

    @Override
    public ChannelFuture write(HeartMsg msg) {
        return write0(msg);
    }

    @Override
    public RealNatBTPChannel flushChannel(Channel channel) {
        flushChannel0(channel);
        return this;
    }

    //====写出信息的方法
    public ChannelFuture writePing() {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new Ping());
        return write(msg);
    }

    public ChannelFuture writeRegisterNatBTP(InetSocketAddress acptUserAddr) {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(
            msg.new NatRegisterBTPChannel()
                .setAcptUserPort(acptUserAddr.getPort())
        );
        return write(msg);
    }

    public RealChannel newRealChannel(Integer userCode) {
        if (userCode == null) {
            return null;
        }
        RealChannel realChannel = new RealChannel(null, userCode);
        realChannelMap.put(userCode, realChannel);
        return realChannel;
    }

    public ChannelFuture writeToReal(byte[] msgBody, Integer userCode) {
        RealChannel realChannel = realChannelMap.get(userCode);
        return realChannel.writeMsgBody(msgBody);
    }

    private ChannelFuture writeRealConnected(Integer userCode) {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new Connected().setUserCode(userCode));
        return write(msg);
    }

    private ChannelFuture writeTransferBody(byte[] bytes, Integer userCode) {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new TransferBody().setMsgBody(bytes).setUserCode(userCode));
        return write(msg);
    }

    //real因为user被动关闭
    public void userChannelClosed(Integer userCode) {
        if (userCode != null) {
            RealChannel realChannel = realChannelMap.remove(userCode);
            if (realChannel != null) {
                realChannel.close();
            }
        }
    }

    //real主动关闭
    private ChannelFuture writeRealChannelClosed(final Integer userCode) {
        if (userCode != null) {
            RealChannel channel = realChannelMap.remove(userCode);
            if (channel != null) {
                HeartMsg msg = new HeartMsg();
                msg.setHeartBody(msg.new Closed().setUserCode(userCode));
                return write(msg);
            }
        }
        return null;
    }

    public class RealChannel extends ProxyChannel<RealChannel, ChannelBuffer> {
        private final Integer userCode;

        public RealChannel(Channel channel, Integer userCode) {
            super(channel);
            this.userCode = userCode;
        }

        @Override
        public ChannelFuture write(ChannelBuffer msg) {
            return write0(msg);
        }

        @Override
        public RealChannel flushChannel(Channel channel) {
            super.flushChannel0(channel);
            return this;
        }

        @Override
        public void close() {
            close0();
        }

        public ChannelFuture writeToNatBTP(byte[] bytes) {
            return RealNatBTPChannel.this.writeTransferBody(bytes, userCode);
        }

        public ChannelFuture writeRealConnected() {
            return RealNatBTPChannel.this.writeRealConnected(userCode);
        }

        public ChannelFuture writeMsgBody(byte[] msgBody) {
            ChannelBuffer buffer = getChannelBufferFactory().getBuffer(msgBody, 0, msgBody.length);
            return write(buffer);
        }

        public ChannelFuture writeRealChannelClosed() {
            return RealNatBTPChannel.this.writeRealChannelClosed(this.userCode);
        }
    }

}
