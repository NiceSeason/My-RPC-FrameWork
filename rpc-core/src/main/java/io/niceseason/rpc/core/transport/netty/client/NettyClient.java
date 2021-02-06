package io.niceseason.rpc.core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.common.util.RpcMessageChecker;
import io.niceseason.rpc.core.RpcClient;
import io.niceseason.rpc.core.codec.CommonDecoder;
import io.niceseason.rpc.core.codec.CommonEncoder;
import io.niceseason.rpc.core.registry.NacosServiceDiscovery;
import io.niceseason.rpc.core.registry.NacosServiceRegistry;
import io.niceseason.rpc.core.registry.ServiceDiscovery;
import io.niceseason.rpc.core.registry.ServiceRegistry;
import io.niceseason.rpc.core.serializer.KryoSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Service;
import java.net.InetSocketAddress;

public class NettyClient implements RpcClient {



    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);


    private final ServiceRegistry serviceRegistry;

    private final ServiceDiscovery serviceDiscovery;

    public NettyClient() {
        serviceRegistry = new NacosServiceRegistry();
        serviceDiscovery = new NacosServiceDiscovery();
    }



    @Override
    public Object sendRequest(RpcRequest request) {
        try{
            InetSocketAddress address = serviceDiscovery.lookupService(request.getInterfaceName());
            Channel channel = ChannelProvider.getChannel(address, new KryoSerializer());
            if (channel.isActive()) {
                ChannelFuture future1 = channel.writeAndFlush(request);
                future1.addListener((ChannelFutureListener) future2 -> {
                    if (future2.isSuccess())
                        logger.info("客户端发送消息:{}",request.toString());
                    else
                        logger.error("发送消息时有错误产生:{}", future1.cause());
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse"+request.getRequestId());
                RpcResponse response = channel.attr(key).get();
                RpcMessageChecker.check(request,response);
                return response;
            }else {
                throw new IllegalStateException("channel未激活");
            }
        } catch (InterruptedException e) {
            logger.error("客户端连接时发生错误:{}",e.getMessage());
        }
        return null;
    }
}
