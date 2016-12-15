package zzy.zyproxy.netnat.nat.tasker;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.task.Task;
import zzy.zyproxy.core.util.task.TaskExecutor;
import zzy.zyproxy.netnat.util.AbstractInboundHandlerEvent;
import zzy.zyproxy.netnat.util.ProxyPacketFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public class TcpRealTasker extends AbstractInboundHandlerEvent<byte[]> {
    private final static Logger LOGGER = LoggerFactory.getLogger(TcpRealTasker.class);
    private ChannelHandlerContext btpCtx;
    private final Integer userCode;


    public TcpRealTasker(ChannelHandlerContext btpCtx, Integer userCode, TaskExecutor shareSingleThreadExecuter) {
        super();
        if (btpCtx == null) {
            throw new NullPointerException("ClientBTPTasker#btpCtx");
        }
        if (userCode == null) {
            throw new NullPointerException("ClientBTPTasker#userCode");
        }
        if (shareSingleThreadExecuter == null) {
            throw new NullPointerException("ClientBTPTasker#shareSingleThreadExecuter");
        }
        ///===
        this.userCode = userCode;
        this.btpCtx = btpCtx;
        taskExecutor = shareSingleThreadExecuter;
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
                taskExecutors.removeShareExecuter(userCode);
                if (tcpUser != null) {//主动关闭
                    btpCtxWriteAndFlush(ProxyPacketFactory.newPacketClose(userCode));
                    taskExecutor.shutdown();
                } else {//被动动关闭
                    taskExecutor.shutdownNow();
                }
            }
        };
        taskExecutor().addLast(task);
    }
}
