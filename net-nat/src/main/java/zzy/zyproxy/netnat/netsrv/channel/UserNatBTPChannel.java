package zzy.zyproxy.netnat.netsrv.channel;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.ProxyChannel;
import zzy.zyproxy.core.packet.heart.HeartMsg;
import zzy.zyproxy.netnat.channel.HeartChannel;
import zzy.zyproxy.netnat.netsrv.ChannelShare;

import java.util.HashMap;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class UserNatBTPChannel {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserNatBTPChannel.class);

    private final ChannelShare channelShare;
    private UserChannel userchannel;
    private NatBTPChannel natBTPChannel;

    public enum UserChannelStatus {
        OPEN,
        CONNECTED,
        READ,
        CLOSE
    }

    private HashMap<UserChannelStatus, Runnable> channelTask = new HashMap<UserChannelStatus, Runnable>();

    public UserNatBTPChannel(Channel userchannel, Channel natBTPChannel, ChannelShare channelShare) {
        this.channelShare = channelShare;
        this.userchannel = new UserChannel(userchannel);
        this.natBTPChannel = new NatBTPChannel(natBTPChannel);
    }

    public NatBTPChannel flushNatBTPChannel(Channel channel) {
        natBTPChannel = natBTPChannel.flushChannel(channel);
        return natBTPChannel;
    }

    public UserChannel flushUserChannel(Channel channel) {
        userchannel = userchannel.flushChannel(channel);
        return userchannel;
    }

    public void close() {
        userchannel.disconnect();
        natBTPChannel.disconnect();
    }

    public class UserChannel extends ProxyChannel<UserChannel> {
        public UserChannel(Channel channel) {
            super(channel);
        }

        @Override
        public UserChannel flushChannel(Channel channel) {
            this.channel = channel;
            return this;
        }

        public ChannelFuture writeToNatBTP(byte[] bytes) {
            HeartMsg msg = new HeartMsg();
            msg.setHeartBody(msg.new UserWriteToNatBTP().setMsgBody(bytes));
            return natBTPChannel.write(msg);
        }

        public ChannelFuture writeChannelConnected(Runnable callback) {
            channelTask.put(UserChannelStatus.CONNECTED, callback);
            HeartMsg msg = new HeartMsg();
            msg.setHeartBody(msg.new UserChannelConnected());
            return natBTPChannel.write(msg);
        }

        public ChannelFuture writeMsgBody(byte[] msgBody) {
            ChannelBuffer buffer = channel.getConfig().getBufferFactory().getBuffer(msgBody, 0, msgBody.length);
            return channel.write(buffer);
        }

        public ChannelFuture writeUserChannelClosed() {
            return natBTPChannel.writeUserChannelClosed();
        }
    }

    public class NatBTPChannel extends HeartChannel<NatBTPChannel> {
        private Integer acptUserPort = null;

        public NatBTPChannel(Channel channel) {
            super(channel);
        }

        @Override
        public NatBTPChannel flushChannel(Channel channel) {
            this.channel = channel;
            return this;
        }

        public ChannelFuture write(HeartMsg msg) {
            return this.channel.write(msg);
        }

        public UserNatBTPChannel getUserNatBTPChannel() {
            return UserNatBTPChannel.this;
        }

        public void realChannelConnected() {
            Runnable runnable = channelTask.get(UserChannelStatus.CONNECTED);
            if (runnable != null) {
                runnable.run();
            }
        }

        public ChannelFuture writeToUser(byte[] msgBody) {
            return userchannel.writeMsgBody(msgBody);
        }

        public ChannelFuture realChannelClosed() {
            ChannelFuture future = userchannel.disconnect();
            recycle(future);
            return future;
        }

        public ChannelFuture writeUserChannelClosed() {
            HeartMsg msg = new HeartMsg();
            msg.setHeartBody(msg.new UserChannelClosed());
            ChannelFuture future = channel.write(msg);
            recycle(future);
            return future;
        }

        public void setAcptUserPort(int acptUserPort) {
            this.acptUserPort = acptUserPort;
        }
    }

    private void recycle(ChannelFuture future) {
        if (future == null) {
            return;
        }
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    flushUserChannel(null);
                    Integer acptUserPort = natBTPChannel.acptUserPort;
                    if (acptUserPort != null && channelShare != null) {
                        LOGGER.debug("recycle @port:{}", acptUserPort);
                        channelShare.putNatBTPChannel(UserNatBTPChannel.this, acptUserPort);
                    }
                }
            }
        });
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("userchannel", userchannel)
            .append("natBTPChannel", natBTPChannel)
            .toString();
    }
}
