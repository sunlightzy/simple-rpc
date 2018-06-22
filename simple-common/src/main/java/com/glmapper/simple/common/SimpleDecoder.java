package com.glmapper.simple.common;

import com.glmapper.simple.common.serializer.FastJsonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * RPC解码
 *
 * @author Jerry
 */
public class SimpleDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public SimpleDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int headerLength = 4;
        if (in.readableBytes() < headerLength) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = FastJsonSerializer.getInstance().deserialize(data, genericClass);
        out.add(obj);
    }
}
