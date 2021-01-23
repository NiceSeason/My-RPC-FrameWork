package io.niceseason.rpc.server;

import io.niceseason.rpc.api.HelloService;
import io.niceseason.rpc.core.server.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService service = new HelloServiceImpl();
        RpcServer server = new RpcServer();
        server.register(7, service);
    }
}
