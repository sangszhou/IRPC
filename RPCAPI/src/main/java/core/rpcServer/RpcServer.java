package core.rpcServer;

import core.protocol.Service;

import java.util.Map;

/**
 * Created by xinszhou on 23/03/2017.
 */
public interface RpcServer {
    void addService();

    Object getRegisteredServices(String serviceName);

    // register service to zookeeper
    void publish();
}
