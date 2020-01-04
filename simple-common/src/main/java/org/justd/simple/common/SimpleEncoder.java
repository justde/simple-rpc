package org.justd.simple.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.justd.simple.common.serializer.FastJsonSerializer;

/**
 * @author Zhangjd
 * @title: SimpleEncoder
 * @description:
 * @date 2020/1/321:19
 */
public class SimpleEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    public SimpleEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)){
            byte[] data = FastJsonSerializer.getInstance().serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
