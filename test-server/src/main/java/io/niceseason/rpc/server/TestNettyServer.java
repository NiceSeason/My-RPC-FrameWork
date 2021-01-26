package io.niceseason.rpc.server;

import io.niceseason.rpc.api.HelloService;
import io.niceseason.rpc.core.RpcServer;
import io.niceseason.rpc.core.netty.server.NettyServer;
import io.niceseason.rpc.core.registry.DefaultServiceRegistry;
import io.niceseason.rpc.core.registry.ServiceRegistry;

public class TestNettyServer {
    public static void main(String[] args) {
        ServiceRegistry registry = new DefaultServiceRegistry();
        HelloService helloService = new HelloServiceImpl();
        registry.register(helloService);
        RpcServer server = new NettyServer();
        server.start(7);
    }
}
