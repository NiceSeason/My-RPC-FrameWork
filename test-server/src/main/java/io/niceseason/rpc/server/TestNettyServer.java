package io.niceseason.rpc.server;

import io.niceseason.rpc.api.BookStore;
import io.niceseason.rpc.api.HelloService;
import io.niceseason.rpc.core.RpcServer;
import io.niceseason.rpc.core.transport.netty.server.NettyServer;

public class TestNettyServer {
    public static void main(String[] args) {
        RpcServer rpcServer = new NettyServer("127.0.0.1", 7);
        HelloService helloService = new HelloServiceImpl();
        rpcServer.publishService(helloService, HelloService.class);
    }
}
