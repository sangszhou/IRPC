package core.rpcClient;

import core.protocol.RpcRequest;
import core.protocol.RpcResponse;
import core.utils.JSONUtils;
import io.netty.channel.*;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xinszhou on 23/03/2017.
 */
//@todo ensure response in order?
public class DefaultRpcClientHandler extends SimpleChannelInboundHandler<RpcResponse>
        implements RpcClientHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    Channel channel;
    EventLoop eventLoop;
    Map<String, Promise<RpcResponse>> inFlightMsg = new ConcurrentHashMap<>();

    @Override
    public Promise<RpcResponse> send(RpcRequest request) {
        Promise<RpcResponse> futureReply = new DefaultPromise<>(eventLoop);
        inFlightMsg.put(request.getRequestId(), futureReply);
        channel.writeAndFlush(request);
        return futureReply;
    }


    // @todo may not receive in order
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {

        if (inFlightMsg.containsKey(msg.getRequestId())) {
            logger.error("message request and response not matching, request: " +
                    JSONUtils.toJson(inFlightMsg) + " response " +
                    JSONUtils.toJson(msg));
        }

        logger.info("request message match response message, request message \n" +
                JSONUtils.toJson(inFlightMsg) + " \n response message \n" +
                JSONUtils.toJson(msg)
        );

        inFlightMsg.get(msg.getRequestId()).setSuccess(msg);
    }
}
