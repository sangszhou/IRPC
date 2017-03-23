package core.rpcServer;

import core.protocol.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by xinszhou on 23/03/2017.
 */
public class DefaultRpcServerHandler extends SimpleChannelInboundHandler<RpcRequest>
        implements RpcServerHandler {

    Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, Object> registeredService;

    public DefaultRpcServerHandler(Map<String, Object> services) {
        registeredService = services;
    }

    @Override
    public Object handle(RpcRequest request) throws Exception {
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Object serviceBean = registeredService.get(className);
        Class<?> cls = serviceBean.getClass();

        // cglib reflection
        FastClass fastClass = FastClass.create(cls);
        FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);

        return fastMethod.invoke(serviceBean, parameters);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        System.out.println("server exception caught");
        System.out.println(throwable.getMessage());
        ctx.close();
    }


}
