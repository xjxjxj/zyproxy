package zzy.zyproxy.netnat.nat.tasker;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.SharaChannels;
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
    private final SharaChannels sharaChannels;
    private ChannelHandlerContext btpCtx;
    private final Integer userCode;


    public TcpRealTasker(ChannelHandlerContext btpCtx, Integer userCode, TaskExecutors taskExecutors, SharaChannels sharaChannels) {
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
        if (sharaChannels == null) {
            throw new NullPointerException("ClientBTPTasker#sharaChannels");
        }
        this.userCode = userCode;
        this.btpCtx = btpCtx;
        this.sharaChannels = sharaChannels;
        taskExecutor = taskExecutors.createSharaSingleThreadExecuter(userCode);
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
    protected Runnable channelReadTask(final ChannelHandlerContext ctx, final byte[] msg) {
        return new Runnable() {
            public void run() {
                btpCtxWriteAndFlush(ProxyPacketFactory.newPacketTransmit(userCode, msg));
            }
        };
    }


    @Override
    protected Runnable channelActiveTask(final ChannelHandlerContext ctx) {
        return new Runnable() {
            public void run() {
                sharaChannels.putTcpUser(userCode, ctx, null);
                taskExecutor.start();//TODO bug
            }
        };
    }

    @Override
    protected Runnable channelInactiveTask(ChannelHandlerContext ctx) {
        return new Runnable() {
            public void run() {
                ChannelHandlerContext tcpUser = sharaChannels.removeTcpUser(userCode);
                if (tcpUser != null) {
                    btpCtxWriteAndFlush(ProxyPacketFactory.newPacketClose(userCode));
                }
            }
        };
    }
}
