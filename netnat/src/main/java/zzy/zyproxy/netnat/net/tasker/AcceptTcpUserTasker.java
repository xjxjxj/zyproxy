package zzy.zyproxy.netnat.net.tasker;

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

import java.net.InetSocketAddress;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public class AcceptTcpUserTasker extends AbstractInboundHandlerEvent<byte[]> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptTcpUserTasker.class);
    private ShareChannels shareChannels;
    private final InetSocketAddress bindAddr;
    private ChannelHandlerContext btpCtx;
    private final Integer userCode;


    public AcceptTcpUserTasker(ShareChannels shareChannels, InetSocketAddress bindAddr, TaskExecutors taskExecutors) {
        if (shareChannels == null) {
            throw new NullPointerException("AcceptBTPTasker#shareChannels");
        }
        if (bindAddr == null) {
            throw new NullPointerException("AcceptBTPTasker#bindAddr");
        }
        if (taskExecutors == null) {
            throw new NullPointerException("AcceptBTPTasker#taskExecutors");
        }
        ////----
        this.shareChannels = shareChannels;
        this.bindAddr = bindAddr;
        this.userCode = this.hashCode();
        taskExecutor = taskExecutors.createShareSingleThreadExecuter(userCode).start();
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
                btpCtx = shareChannels.putTcpUser(userCode, ctx, bindAddr.getPort());
                btpCtxWriteAndFlush(ProxyPacketFactory.newPacketConnected(userCode));
            }
        };
        taskExecutor().addLast(task);
    }

    @Override
    public void channelInactiveEvent(ChannelHandlerContext ctx) {
        Task task = new Task() {
            public void run() {
                ChannelHandlerContext tcpUser = shareChannels.removeTcpUser(userCode);
                LOGGER.info("channelInactiveEvent, userCode:{},{}", userCode,tcpUser == null);
                if (tcpUser != null) {
                    btpCtxWriteAndFlush(ProxyPacketFactory.newPacketClose(userCode));
                }
            }
        };
        taskExecutor().addLast(task);
    }
}
