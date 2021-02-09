package io.niceseason.rpc.client;

import io.niceseason.rpc.api.Book;
import io.niceseason.rpc.api.BookStore;
import io.niceseason.rpc.api.HelloObject;
import io.niceseason.rpc.api.HelloService;
import io.niceseason.rpc.core.RpcClient;
import io.niceseason.rpc.core.RpcClientProxy;
import io.niceseason.rpc.core.transport.netty.client.NettyClient;

public class TestNettyClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService proxy1 = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(1, "sada");
        for (int i = 0; i < 10; i++) {
            System.out.println(proxy1.hello(object));
        }
    }
}
