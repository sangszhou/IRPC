package core.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by xinszhou on 23/03/2017.
 */
public class RpcEncoder extends MessageToByteEncoder  {

    private Class<?> genericClass;
    private KryoSerialization kryo;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
        kryo = new KryoSerialization();
        // 注册的过程是全局共享的吗
        kryo.register(genericClass);
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		byte[] body=kryo.Serialize(msg);
		out.writeInt(body.length);
		out.writeBytes(body);
    }
}
