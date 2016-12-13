package zzy.zyproxy.netnat.net.tasker;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.ChannelUtil;
import zzy.zyproxy.core.util.SharaChannels;
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
    private SharaChannels sharaChannels;


    public AcceptBTPTasker(SharaChannels sharaChannels, TaskExecutors taskExecutors) {
        this.sharaChannels = sharaChannels;
        this.taskExecutors = taskExecutors;
        taskExecutor = taskExecutors.createExclusiveSingleThreadExecuter();
    }

    public Logger subLogger() {
        return LOGGER;
    }

    //自己的业务队列线程
    //用户的业务队列通过get的方式
    private final TaskExecutor taskExecutor;

    public TaskExecutor taskExecutor() {
        return taskExecutor;
    }

    @Override
    protected Runnable channelReadTask(final ChannelHandlerContext ctx, final ProxyPacket msg) {
        return new Runnable() {
            public void run() {
                if (msg.isAuth()) {
                    ProxyPacket.Auth auth = msg.asAuth();
                    sharaChannels.putTcpBtp(auth.getAuthCode(), ctx);
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
                    userTaskExector.submitQueueTask(new Runnable() {
                        public void run() {
                            ChannelHandlerContext tcpUser = sharaChannels.getTcpUser(userCode);
                            tcpUser.writeAndFlush(body == null ? Unpooled.EMPTY_BUFFER : body);
                        }
                    });
                    return;
                }
                if (msg.isClose()) {
                    ProxyPacket.Close close = msg.asClose();
                    final Integer userCode = close.getUserCode();
                    TaskExecutor userTaskExector = taskExecutors.getTaskExector(userCode);
                    userTaskExector.submitQueueTask(new Runnable() {
                        public void run() {
                            ChannelHandlerContext userCtx = sharaChannels.removeTcpUser(userCode);
                            if (userCtx != null) {
                                ChannelUtil.flushAndClose(userCtx);
                            }
                        }
                    });
                }
            }
        };
    }

}
