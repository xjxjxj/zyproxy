package zzy.zyproxy.core.util;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhouzhongyuan
 * @date 2016/12/6
 */
public abstract class ChannelUtil {
    public static void flushAndClose(Channel channel){
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    } 
    public static void flushAndClose(ChannelHandlerContext channel){
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
