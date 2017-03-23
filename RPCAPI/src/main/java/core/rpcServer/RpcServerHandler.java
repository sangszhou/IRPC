package core.rpcServer;

import core.protocol.RpcRequest;

/**
 * Created by xinszhou on 23/03/2017.
 */
public interface RpcServerHandler  {
    Object handle(RpcRequest request) throws Exception;
}
