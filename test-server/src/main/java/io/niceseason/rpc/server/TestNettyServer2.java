package io.niceseason.rpc.server;

import io.niceseason.rpc.api.HelloService;
import io.niceseason.rpc.core.RpcServer;
import io.niceseason.rpc.core.transport.netty.server.NettyServer;

public class TestNettyServer2 {
    public static void main(String[] args) {
        RpcServer rpcServer2 = new NettyServer("127.0.0.1", 1234);
        HelloService helloService1 = new HelloServiceImpl2();
        rpcServer2.publishService(helloService1, HelloService.class);
    }
}
