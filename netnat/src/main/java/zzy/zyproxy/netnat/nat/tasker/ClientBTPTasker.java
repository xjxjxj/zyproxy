package zzy.zyproxy.netnat.nat.tasker;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.ChannelUtil;
import zzy.zyproxy.core.util.ShareChannels;
import zzy.zyproxy.core.util.task.Task;
import zzy.zyproxy.core.util.task.TaskExecutor;
import zzy.zyproxy.core.util.task.TaskExecutors;
import zzy.zyproxy.netnat.nat.RealClientFactory;
import zzy.zyproxy.netnat.util.AbstractInboundHandlerEvent;
import zzy.zyproxy.netnat.util.ProxyPacketFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public class ClientBTPTasker extends AbstractInboundHandlerEvent<ProxyPacket> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientBTPTasker.class);

    public Logger subLogger() {
        return LOGGER;
    }

    private final ShareChannels shareChannels;
    private final RealClientFactory realClientFactory;
    private final String auth;
    private final TaskExecutors taskExecutors;

    private final Map<Integer, TcpRealTasker> tcpRealTaskerMap = new HashMap<Integer, TcpRealTasker>();


    public ClientBTPTasker(ShareChannels shareChannels, RealClientFactory realClientFactory, String auth, TaskExecutors taskExecutors) {
        super();
        if (shareChannels == null) {
            throw new NullPointerException("ClientBTPTasker#shareChannels");
        }
        if (realClientFactory == null) {
            throw new NullPointerException("ClientBTPTasker#realClientFactory");
        }
        if (auth == null) {
            throw new NullPointerException("ClientBTPTasker#auth");
        }
        if (taskExecutors == null) {
            throw new NullPointerException("ClientBTPTasker#taskExecutors");
        }
        //---
        this.shareChannels = shareChannels;
        this.realClientFactory = realClientFactory;
        this.auth = auth;
        this.taskExecutors = taskExecutors;
        this.taskExecutor = taskExecutors.createExclusiveSingleThreadExecuter().start();
    }


    //自己的业务队列线程
    //用户的业务队列通过get的方式
    private final TaskExecutor taskExecutor;

    public TaskExecutor taskExecutor() {
        return taskExecutor;
    }


    @Override
    public void channelActiveEvent(final ChannelHandlerContext ctx) {
        Task task = new Task() {
            public void run() {
                shareChannels.putTcpBtp(null, ctx);
                ctx.writeAndFlush(ProxyPacketFactory.newPacketAuth(auth));
            }
        };
        taskExecutor().addFirst(task);
    }

    @Override
    public void channelReadEvent(final ChannelHandlerContext ctx, final ProxyPacket msg) {
        Task task = new Task() {
            public void run() {
                if (msg.isAuth()) {
                    //ProxyPacket.Auth auth = msg.asAuth();
                    //***do noting***
                    return;
                }
                if (msg.isConnected()) {
                    ProxyPacket.Connected connected = msg.asConnected();
                    Integer userCode = connected.getUserCode();
                    final TcpRealTasker tcpRealTasker
                        = new TcpRealTasker(ctx
                        , userCode
                        , taskExecutors.createShareSingleThreadExecuter(userCode));

                    tcpRealTaskerMap.put(userCode, tcpRealTasker);
                    realClientFactory.addTcpRealTaskerQueue(tcpRealTasker);
                    try {
                        realClientFactory.createClient();
                    } catch (InterruptedException e) {
                        ctx.writeAndFlush(ProxyPacketFactory.newPacketException(userCode, e.getMessage()));
                    }
                    return;
                }
                if (msg.isTransmit()) {
                    final ProxyPacket.Transmit transmit = msg.asTransmit();
                    final Integer userCode = transmit.getUserCode();
                    TaskExecutor realExector
                        = taskExecutors.getShareTaskExector(userCode);
                    if (realExector != null) {
                        realExector.addLast(new Task() {
                            public void run() {
                                ChannelHandlerContext tcpUser = shareChannels.getTcpUser(userCode);
                                if (tcpUser != null) {
                                    byte[] body = transmit.getBody();
                                    tcpUser.writeAndFlush(body == null ? Unpooled.EMPTY_BUFFER : Unpooled.wrappedBuffer(body));
                                }
                            }
                        });
                    }
                    return;
                }
                if (msg.isClose()) {
                    ProxyPacket.Close close = msg.asClose();
                    final Integer userCode = close.getUserCode();
                    TaskExecutor realExector
                        = taskExecutors.getShareTaskExector(userCode);
                    if (realExector != null) {
                        realExector.addLast(new Task() {
                            public void run() {
                                ChannelHandlerContext userCtx = shareChannels.removeTcpUser(userCode);
                                if (userCtx != null) {
                                    ChannelUtil.flushAndClose(userCtx);
                                }
                            }
                        });
                    }
                }
                if (msg.isHeart()) {
                    //ProxyPacket.Heart heart = msg.asHeart();
                    //do noting
                    return;
                }
                if (msg.isException()) {
                    ProxyPacket.Exception exception = msg.asException();
                    //TODO 异常的处理
                }
            }
        };
        taskExecutor().addLast(task);
    }

    @Override
    public void userIdleStateEvent(ChannelHandlerContext ctx, IdleStateEvent evt) {
        if (IdleState.ALL_IDLE.equals(evt.state())) {
            System.out.println(taskExecutors.getShareTaskExectorUserCodes().length + "{||}" + shareChannels.getTcpUsers().length);
            ctx.writeAndFlush(ProxyPacketFactory.newPacketHeart(taskExecutors.getShareTaskExectorUserCodes()));
        }
    }
}
