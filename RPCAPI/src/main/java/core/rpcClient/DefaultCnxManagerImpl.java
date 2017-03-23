package core.rpcClient;

import core.protocol.Service;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xinszhou on 23/03/2017.
 */
public class DefaultCnxManagerImpl implements CnxManager {
    Logger log = LoggerFactory.getLogger(getClass());

    public static CnxManager cnxManager = new DefaultCnxManagerImpl();
    private static AtomicInteger roundRobin = new AtomicInteger(0);

    // multi-connections per service
    Map<Service, List<RpcClientHandler>> connections = new ConcurrentHashMap<>();

    // from className to service
    Map<String, Service> serviceMapping = new ConcurrentHashMap<>();

    private ExecutorService threadPoolExecutor;

    EventLoopGroup group;

    public DefaultCnxManagerImpl() {
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 7, TimeUnit.DAYS, new LinkedBlockingDeque<Runnable>(20), new DefaultThreadFactory("connection-manager-pool"));
        group = new NioEventLoopGroup(4, new DefaultThreadFactory("connection-manager-pool"));
    }

    public RpcClientHandler getHandler(String clzName) {
        Service service = serviceMapping.get(clzName);
        if (service == null) {
            log.error("failed to get handler for class " + clzName +", service may not register yet");
        }

        List<RpcClientHandler> handlers = connections.get(service);
        if (handlers == null) {
            log.error("failed to find handler for class " + clzName + ", handler may not connected yet");
        }

        int index = roundRobin.getAndIncrement() % handlers.size();

        return handlers.get(index);
    }

    public RpcClientHandler getHandler(Class clz) {
        return getHandler(clz.getName());
    }

    // load config and connect to them
    @Override
    public void loadService() {
        //read from external 3rd party or create it manually
        //support reload in the future


    }
}
