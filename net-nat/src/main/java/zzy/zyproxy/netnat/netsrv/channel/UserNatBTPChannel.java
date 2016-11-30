package zzy.zyproxy.netnat.netsrv.channel;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.channel.ProxyChannel;
import zzy.zyproxy.core.packet.heart.HeartMsg;

import java.util.HashMap;

/**
 * @author zhouzhongyuan
 * @date 2016/11/27
 */
public class UserNatBTPChannel extends ProxyChannel<UserNatBTPChannel, HeartMsg> {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserNatBTPChannel.class);

    private final HashMap<Integer, UserChannel> userChannelMap = new HashMap<Integer, UserChannel>();


    public UserNatBTPChannel(Channel channel) {
        super(channel);
    }

    @Override
    public ChannelFuture write(HeartMsg msg) {
        return write0(msg);
    }

    @Override
    public UserNatBTPChannel flushChannel(Channel channel) {
        flushChannel0(channel);
        return this;
    }

    @Override
    public void close() {
        LOGGER.error("UserNatBTPChannel#close!!");
    }

    //===写出信息的方法
    public ChannelFuture writePong() {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new Pong());
        return write(msg);
    }

    //====
    private ChannelFuture writeUserConnected(UserChannel userChannel) {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new Connected().setUserCode(userChannel.hashCode()));
        return write(msg);
    }

    public int getUserSize() {
        return userChannelMap.size();
    }

    public UserChannel newUserChannel(Channel channel) {
        int userCode = channel.hashCode();
        UserChannel userChannel = new UserChannel(channel, userCode);
        userChannelMap.put(userCode, userChannel);
        return userChannel;
    }

    public void realConnected(Integer userCode) {
        if (userCode == null) {
            throw new NullPointerException("realConnected userCode为null");
        }
        UserChannel runnable = userChannelMap.get(userCode);
        if (runnable != null) {
            Runnable connectedTask = runnable.connectedTask;
            if (connectedTask != null) {
                connectedTask.run();
            }
        }
    }

    public ChannelFuture writeToUser(byte[] msgBody, Integer userCode) {
        if (userCode == null) {
            throw new NullPointerException("writeToUser 断开 userCode为null");
        }
        UserChannel userChannel = userChannelMap.get(userCode);
        if (userChannel == null) {
            throw new NullPointerException("writeToUser 找不到 userChannel");
        }
        return userChannel.writeMsgBody(msgBody);
    }

    private ChannelFuture writeTransferBody(byte[] bytes, Integer userCode) {
        HeartMsg msg = new HeartMsg();
        msg.setHeartBody(msg.new TransferBody().setMsgBody(bytes).setUserCode(userCode));
        return write(msg);
    }

    //user因为real被动关闭
    public void realChannelClosed(Integer userCode) {
        if (userCode != null) {
            UserChannel userChannel = userChannelMap.remove(userCode);
            if (userChannel != null) {
                userChannel.close();
            }
        }
    }

    //user主动关闭发送信息给real
    private ChannelFuture writeUserChannelClosed(final Integer userCode) {
        if (userCode != null) {
            UserChannel channel = userChannelMap.remove(userCode);
            if (channel != null) {
                HeartMsg msg = new HeartMsg();
                msg.setHeartBody(msg.new Closed().setUserCode(userCode));
                return write(msg);
            }
        }
        return null;
    }

    public class UserChannel extends ProxyChannel<UserChannel, ChannelBuffer> {
        Integer userCode;
        Runnable connectedTask = null;

        public UserChannel(Channel channel, int userCode) {
            super(channel);
            this.userCode = userCode;
        }

        @Override
        public ChannelFuture write(ChannelBuffer msg) {
            return write0(msg);
        }

        @Override
        public UserChannel flushChannel(Channel channel) {
            flushChannel0(channel);
            return this;
        }

        @Override
        public void close() {
            close0();
        }

        public ChannelFuture writeToNatBTP(byte[] bytes) {
            return UserNatBTPChannel.this.writeTransferBody(bytes, userCode);
        }

        public ChannelFuture writeConnected(Runnable connectedTask) {
            this.connectedTask = connectedTask;
            return UserNatBTPChannel.this.writeUserConnected(this);
        }

        private ChannelFuture writeMsgBody(byte[] msgBody) {
            ChannelBuffer buffer = getChannelBufferFactory().getBuffer(msgBody, 0, msgBody.length);
            return write(buffer);
        }

        public ChannelFuture writeUserChannelClosed() {
            return UserNatBTPChannel.this.writeUserChannelClosed(userCode);
        }
    }
}
