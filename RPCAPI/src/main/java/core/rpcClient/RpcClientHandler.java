package core.rpcClient;

import core.protocol.RpcRequest;
import core.protocol.RpcResponse;
import io.netty.util.concurrent.Promise;

/**
 * Created by xinszhou on 23/03/2017.
 */
public interface RpcClientHandler {
    Promise<RpcResponse> send(RpcRequest request);
}
