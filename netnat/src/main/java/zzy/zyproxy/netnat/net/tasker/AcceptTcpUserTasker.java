package zzy.zyproxy.netnat.net.tasker;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.SharaChannels;
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
    private SharaChannels sharaChannels;
    private final InetSocketAddress bindAddr;
    private ChannelHandlerContext btpCtx;
    private final Integer userCode;


    public AcceptTcpUserTasker(SharaChannels sharaChannels, InetSocketAddress bindAddr, TaskExecutors taskExecutors) {
        this.sharaChannels = sharaChannels;
        this.bindAddr = bindAddr;
        this.userCode = this.hashCode();
        taskExecutor = taskExecutors.createSharaSingleThreadExecuter(userCode).start();
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
                btpCtx = sharaChannels.putTcpUser(userCode, ctx, bindAddr.getPort());
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
