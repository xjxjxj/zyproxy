package zzy.zyproxy.core.codec.msgpackcodec;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouzhongyuan
 * @date 2016/11/24
 */
public abstract class MsgPackCodecFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgPackCodecFactory.class);

    public static ChannelPipeline addDecoderAtLast(ChannelPipeline channelPipeline) {
        channelPipeline.addLast(
            LengthFieldBasedFrameDecoder.class.getName(),
            new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 4, 4)
        );
        channelPipeline.addLast(MsgPackDecoder.class.getName(), new MsgPackDecoder());
        return channelPipeline;
    }

    public static ChannelPipeline addEncoderAtLast(ChannelPipeline channelPipeline) {
        channelPipeline.addLast(LengthFieldPrepender.class.getName(), new LengthFieldPrepender(4));
        channelPipeline.addLast(MsgPackEncoder.class.getName(), new MsgPackEncoder());
        return channelPipeline;
    }

}
