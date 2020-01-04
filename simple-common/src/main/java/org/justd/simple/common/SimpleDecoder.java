package org.justd.simple.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author Zhangjd
 * @title: SimpleDecoder
 * @description:
 * @date 2020/1/320:56
 */
public class SimpleDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public SimpleDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        int headerLength = 4;
        if (byteBuf.readableBytes() < headerLength){
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (dataLength < 0){
            ctx.close();
        }

        if (byteBuf.readableBytes() < dataLength){
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);

    }
}
