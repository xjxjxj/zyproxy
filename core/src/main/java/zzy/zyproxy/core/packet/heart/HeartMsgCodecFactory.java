package zzy.zyproxy.core.packet.heart;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zzy.zyproxy.core.codec.msgpackcodec.MsgPackDecoder;
import zzy.zyproxy.core.codec.msgpackcodec.MsgPackEncoder;
import zzy.zyproxy.core.util.ChannelPiplineUtil;

/**
 * @author zhouzhongyuan
 * @date 2016/11/26
 */
public class HeartMsgCodecFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(HeartMsgCodecFactory.class);

    public static ChannelPipeline addDecoderAtLast(ChannelPipeline channelPipeline) {
        ChannelPiplineUtil.addLast(channelPipeline,
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                new MsgPackDecoder(HeartMsg.class));
        return channelPipeline;
    }

    public static ChannelPipeline addEncoderAtLast(ChannelPipeline channelPipeline) {
        ChannelPiplineUtil.addLast(channelPipeline,
                new LengthFieldPrepender(4),
                new MsgPackEncoder(HeartMsg.class));
        return channelPipeline;
    }
}
