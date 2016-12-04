package zzy.zyproxy.core.packet.msgpacket;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import zzy.zyproxy.core.codec.msgpack.MsgPackDecoder;
import zzy.zyproxy.core.codec.msgpack.MsgPackEncoder;

/**
 * @author zhouzhongyuan
 * @date 2016/12/2
 */
public abstract class MsgPackCodec {
    public static ChannelPipeline addCodec(ChannelPipeline channelPipeline) {
        return channelPipeline.addLast(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                new MsgPackDecoder().setClazz(MsgPacket.class),
                new LengthFieldPrepender(4),
                new MsgPackEncoder()
        );
    }
}
