package io.niceseason.rpc.server;

import io.niceseason.rpc.api.BookStore;
import io.niceseason.rpc.api.HelloService;
import io.niceseason.rpc.core.RpcServer;
import io.niceseason.rpc.core.registry.DefaultServiceRegistry;
import io.niceseason.rpc.core.registry.ServiceRegistry;
import io.niceseason.rpc.core.socket.server.SocketServer;

public class TestSocketServer {
    public static void main(String[] args) {
        HelloService service = new HelloServiceImpl();
        BookStore bookStore = new BookStoreImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(service);
        serviceRegistry.register(bookStore);
        RpcServer server = new SocketServer(serviceRegistry);
        server.start(7);
    }
}
