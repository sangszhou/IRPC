package core.rpcServer;

import core.protocol.RpcRequest;
import core.protocol.RpcResponse;
import core.protocol.Service;
import core.rpcClient.DefaultRpcClientHandler;
import core.serialization.RpcDecoder;
import core.serialization.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xinszhou on 23/03/2017.
 */
public class DefaultRpcServer implements RpcServer {
    Logger log = LoggerFactory.getLogger(getClass());

    Map<String, Object> handlerMap = new HashMap<>();

    @Override
    public void addService() {
    }

    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new DefaultRpcServerHandler(handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String host = "127.0.0.1";
            int port = 9999;

            ChannelFuture future = bootstrap.bind(host, port).sync();
            System.out.println("Rpc server started on port " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("failed to establish server", e);
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public Object getRegisteredServices(String serviceName) {
        return null;
    }

    @Override
    public void publish() {

    }
}
