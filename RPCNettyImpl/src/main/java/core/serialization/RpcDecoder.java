package core.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

/**
 * Created by xinszhou on 23/03/2017.
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;
    private KryoSerialization kryo;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
        kryo = new KryoSerialization();
        // 注册的过程是全局共享的吗
        kryo.register(genericClass);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        byteBuf.markReaderIndex(); // 做个标记，等待以后返回
        int dataLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);

        Object obj = kryo.Deserialize(data);
        out.add(obj);
    }


}
