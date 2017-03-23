package core.rpcClient;

import core.protocol.RpcRequest;
import core.protocol.RpcResponse;
import io.netty.util.concurrent.Promise;

import javax.xml.ws.Response;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xinszhou on 23/03/2017.
 */
public class RpcClient implements InvocationHandler {

    private Class<?> proxiedClz;
    CnxManager cnxManager;

    private int timeout;
    private static AtomicLong callTimes = new AtomicLong(0L);

    public Object instance() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{this.proxiedClz}, this);
    }

    public RpcClient represent(Class<?> proxiedClz) {
        this.proxiedClz = proxiedClz;
        return this;
    }

    public RpcClient clientTimeout(int clientTimeout) {
        this.timeout = clientTimeout;
        return this;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<String> parameterTypes = new LinkedList<String>();
        for (Class<?> parameterType : method.getParameterTypes()) {
            parameterTypes.add(parameterType.getName());
        }
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());

        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        Promise<RpcResponse> responseFuture;

        RpcClientHandler handler = cnxManager.getHandler(method.getDeclaringClass().getName());
        responseFuture = handler.send(request);

        if(method.getReturnType().getName().equals("Promise")) {
            return responseFuture;
        } else {
            if(responseFuture.await(timeout, TimeUnit.MILLISECONDS)) {
                return responseFuture.get();
            } else {
                return null;
            }
        }
    }
}
