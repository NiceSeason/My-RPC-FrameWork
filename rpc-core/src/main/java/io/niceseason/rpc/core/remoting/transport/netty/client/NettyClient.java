package io.niceseason.rpc.core.remoting.transport.netty.client;

import io.netty.channel.*;
import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.common.factory.SingletonFactory;
import io.niceseason.rpc.core.loadbalancer.RandomLoadBalancer;
import io.niceseason.rpc.core.registry.NacosServiceDiscovery;
import io.niceseason.rpc.core.registry.NacosServiceRegistry;
import io.niceseason.rpc.core.registry.ServiceDiscovery;
import io.niceseason.rpc.core.registry.ServiceRegistry;
import io.niceseason.rpc.core.remoting.transport.RpcClient;
import io.niceseason.rpc.core.serializer.KryoSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);


    private final ServiceRegistry serviceRegistry;

    private final ServiceDiscovery serviceDiscovery;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        serviceRegistry = new NacosServiceRegistry();
        serviceDiscovery = new NacosServiceDiscovery(new RandomLoadBalancer());
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }



    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest request) {
        CompletableFuture<RpcResponse> completableFuture = new CompletableFuture<>();
        InetSocketAddress address = serviceDiscovery.lookupService(request.getInterfaceName());
        Channel channel = ChannelProvider.getChannel(address, new KryoSerializer());
        if (channel.isActive()) {
            unprocessedRequests.put(request.getRequestId(), completableFuture);
            channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info("客户端发送消息:{}", request.toString());
                } else {
                    completableFuture.completeExceptionally(future.cause());
                    logger.error("发送消息时有错误产生:{}", future.cause());
                }
            });
        }else {
            throw new IllegalStateException("channel未激活");
        }
        return completableFuture;
    }
}
