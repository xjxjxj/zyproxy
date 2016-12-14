package zzy.zyproxy.netnat.nat.tasker;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.ShareChannels;
import zzy.zyproxy.core.util.task.Task;
import zzy.zyproxy.core.util.task.ShareTaskExecutor;
import zzy.zyproxy.core.util.task.TaskExecutor;
import zzy.zyproxy.core.util.task.TaskExecutors;
import zzy.zyproxy.netnat.util.AbstractInboundHandlerEvent;
import zzy.zyproxy.netnat.util.ProxyPacketFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public class TcpRealTasker extends AbstractInboundHandlerEvent<byte[]> {
    private final static Logger LOGGER = LoggerFactory.getLogger(TcpRealTasker.class);
    private final ShareChannels shareChannels;
    private ChannelHandlerContext btpCtx;
    private final Integer userCode;


    public TcpRealTasker(ChannelHandlerContext btpCtx, Integer userCode, TaskExecutors taskExecutors, ShareChannels shareChannels) {
        super();
        if (btpCtx == null) {
            throw new NullPointerException("ClientBTPTasker#btpCtx");
        }
        if (userCode == null) {
            throw new NullPointerException("ClientBTPTasker#userCode");
        }
        if (taskExecutors == null) {
            throw new NullPointerException("ClientBTPTasker#taskExecutors");
        }
        if (shareChannels == null) {
            throw new NullPointerException("ClientBTPTasker#shareChannels");
        }
        ///===
        this.userCode = userCode;
        this.btpCtx = btpCtx;
        this.shareChannels = shareChannels;
        taskExecutor = taskExecutors.createShareSingleThreadExecuter(userCode);
    }

    public Logger subLogger() {
        return LOGGER;
    }

    private final TaskExecutor taskExecutor;

    public TaskExecutor taskExecutor() {
        return taskExecutor;
    }

    private void btpCtxWriteAndFlush(ProxyPacket proxyPacket) {
        btpCtx.writeAndFlush(proxyPacket);
    }

    @Override
    public void channelReadEvent(final ChannelHandlerContext ctx, final byte[] msg) {
        Task task = new Task() {
            public void run() {
                btpCtxWriteAndFlush(ProxyPacketFactory.newPacketTransmit(userCode, msg));
            }
        };
        taskExecutor().addLast(task);
    }


    @Override
    public void channelActiveEvent(final ChannelHandlerContext ctx) {
        Task task = new Task() {
            public void run() {
                shareChannels.putTcpUser(userCode, ctx, null);
            }
        };
        taskExecutor().addFirst(task);
        taskExecutor().start();
    }

    @Override
    public void channelInactiveEvent(ChannelHandlerContext ctx) {
        Task task = new Task() {
            public void run() {
                ChannelHandlerContext tcpUser = shareChannels.removeTcpUser(userCode);
                if (tcpUser != null) {
                    btpCtxWriteAndFlush(ProxyPacketFactory.newPacketClose(userCode));
                }
            }
        };
        taskExecutor().addLast(task);
    }
}
