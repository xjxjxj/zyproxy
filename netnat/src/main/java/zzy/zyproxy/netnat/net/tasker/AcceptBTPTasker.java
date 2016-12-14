package zzy.zyproxy.netnat.net.tasker;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.ChannelUtil;
import zzy.zyproxy.core.util.ShareChannels;
import zzy.zyproxy.core.util.task.Task;
import zzy.zyproxy.core.util.task.ShareTaskExecutor;
import zzy.zyproxy.core.util.task.TaskExecutor;
import zzy.zyproxy.core.util.task.TaskExecutors;
import zzy.zyproxy.netnat.util.AbstractInboundHandlerEvent;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public class AcceptBTPTasker extends AbstractInboundHandlerEvent<ProxyPacket> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcceptBTPTasker.class);
    private final TaskExecutors taskExecutors;
    private ShareChannels shareChannels;


    public AcceptBTPTasker(ShareChannels shareChannels, TaskExecutors taskExecutors) {
        if (shareChannels == null) {
            throw new NullPointerException("AcceptBTPTasker#shareChannels");
        }
        if (taskExecutors == null) {
            throw new NullPointerException("AcceptBTPTasker#taskExecutors");
        }
        ////----
        this.shareChannels = shareChannels;
        this.taskExecutors = taskExecutors;
        this.taskExecutor = taskExecutors.createExclusiveSingleThreadExecuter().start();
    }

    public Logger subLogger() {
        return LOGGER;
    }

    private final TaskExecutor taskExecutor;

    public TaskExecutor taskExecutor() {
        return taskExecutor;
    }

    @Override
    public void channelReadEvent(final ChannelHandlerContext ctx, final ProxyPacket msg) {
        Task task = new Task() {
            public void run() {
                if (msg.isAuth()) {
                    ProxyPacket.Auth auth = msg.asAuth();
                    shareChannels.putTcpBtp(auth.getAuthCode(), ctx);
                    ctx.writeAndFlush(auth.getAuthCode());
                    return;
                }
                if (msg.isConnected()) {
                    //ProxyPacket.Connected connected = msg.asConnected();
                    //***do noting***
                    return;
                }
                if (msg.isTransmit()) {
                    ProxyPacket.Transmit transmit = msg.asTransmit();
                    final byte[] body = transmit.getBody();
                    final Integer userCode = transmit.getUserCode();
                    TaskExecutor userTaskExector = taskExecutors.getTaskExector(userCode);
                    userTaskExector.addLast(new Task() {
                        public void run() {
                            ChannelHandlerContext tcpUser = shareChannels.getTcpUser(userCode);
                            if (tcpUser != null) {
                                tcpUser.writeAndFlush(body == null ? Unpooled.EMPTY_BUFFER : body);
                            }
                        }
                    });
                    return;
                }
                if (msg.isClose()) {
                    ProxyPacket.Close close = msg.asClose();
                    final Integer userCode = close.getUserCode();
                    TaskExecutor userTaskExector = taskExecutors.getTaskExector(userCode);
                    userTaskExector.addLast(new Task() {
                        public void run() {
                            ChannelHandlerContext userCtx = shareChannels.removeTcpUser(userCode);
                            LOGGER.info("msg.isClose(), userCode:{},{}", userCode,userCtx == null);
                            if (userCtx != null) {
                                ChannelUtil.flushAndClose(userCtx);
                            }
                        }
                    });
                }
            }
        };
        taskExecutor().addLast(task);
    }

}
