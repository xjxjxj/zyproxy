package zzy.zyproxy.netnat.nat.tasker;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.packet.ProxyPacket;
import zzy.zyproxy.core.util.ChannelUtil;
import zzy.zyproxy.core.util.SharaChannels;
import zzy.zyproxy.core.util.task.TaskExecutor;
import zzy.zyproxy.core.util.task.TaskExecutors;
import zzy.zyproxy.netnat.nat.RealClientFactory;
import zzy.zyproxy.netnat.util.AbstractInboundHandlerEvent;
import zzy.zyproxy.netnat.util.ProxyPacketFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/12/13
 */
public class ClientBTPTasker extends AbstractInboundHandlerEvent<ProxyPacket> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientBTPTasker.class);

    public Logger subLogger() {
        return LOGGER;
    }

    private final SharaChannels sharaChannels;
    private final RealClientFactory realClientFactory;
    private final String auth;
    private final TaskExecutors taskExecutors;


    public ClientBTPTasker(SharaChannels sharaChannels, RealClientFactory realClientFactory, String auth, TaskExecutors taskExecutors) {
        super();
        if (sharaChannels == null) {
            throw new NullPointerException("ClientBTPTasker#sharaChannels");
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
        this.sharaChannels = sharaChannels;
        this.realClientFactory = realClientFactory;
        this.auth = auth;
        this.taskExecutors = taskExecutors;
        this.taskExecutor = taskExecutors.createExclusiveSingleThreadExecuter();
    }


    //自己的业务队列线程
    //用户的业务队列通过get的方式
    private final TaskExecutor taskExecutor;

    public TaskExecutor taskExecutor() {
        return taskExecutor;
    }


    @Override
    protected Runnable channelActiveTask(final ChannelHandlerContext ctx) {
        return new Runnable() {
            public void run() {
                sharaChannels.putTcpBtp(null, ctx);
                ctx.writeAndFlush(ProxyPacketFactory.newPacketAuth(auth));
            }
        };
    }

    @Override
    protected Runnable channelReadTask(final ChannelHandlerContext ctx, final ProxyPacket msg) {
        return new Runnable() {
            public void run() {
                if (msg.isAuth()) {
                    //ProxyPacket.Auth auth = msg.asAuth();
                    //***do noting***
                    return;
                }
                if (msg.isConnected()) {
                    ProxyPacket.Connected connected = msg.asConnected();
                    final TcpRealTasker tcpRealTasker 
                        = new TcpRealTasker(ctx, connected.getUserCode(), taskExecutors, sharaChannels);
                    realClientFactory.addTcpRealTaskerQueue(tcpRealTasker);
                    try {
                        realClientFactory.createClient();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (msg.isTransmit()) {
                    final ProxyPacket.Transmit transmit = msg.asTransmit();
                    final Integer userCode = transmit.getUserCode();
                    TaskExecutor realExector
                        = taskExecutors.getTaskExector(userCode);
                    realExector.submitQueueTask(new Runnable() {
                        public void run() {
                            sharaChannels.getTcpUser(userCode).writeAndFlush(transmit.getBody());
                        }
                    });
                    return;
                }
                if (msg.isClose()) {
                    ProxyPacket.Close close = msg.asClose();
                    final Integer userCode = close.getUserCode();
                    TaskExecutor realExector
                        = taskExecutors.getTaskExector(userCode);
                    realExector.submitQueueTask(new Runnable() {
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
