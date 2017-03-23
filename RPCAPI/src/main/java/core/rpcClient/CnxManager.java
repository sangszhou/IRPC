package core.rpcClient;

import core.protocol.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xinszhou on 23/03/2017.
 */
public interface CnxManager {

    RpcClientHandler getHandler(String clzName);
    RpcClientHandler getHandler(Class clz);
    void loadService();

}
