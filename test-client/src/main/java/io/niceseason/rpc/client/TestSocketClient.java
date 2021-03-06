package io.niceseason.rpc.client;

import io.niceseason.rpc.api.Book;
import io.niceseason.rpc.api.BookStore;
import io.niceseason.rpc.api.HelloObject;
import io.niceseason.rpc.api.HelloService;
import io.niceseason.rpc.core.remoting.transport.RpcClient;
import io.niceseason.rpc.core.proxy.RpcClientProxy;
import io.niceseason.rpc.core.remoting.transport.socket.client.SocketClient;

public class TestSocketClient {
    public static void main(String[] args) {
        RpcClient rpcClient = new SocketClient("127.0.0.1", 7);
        RpcClientProxy proxy = new RpcClientProxy(rpcClient);
        HelloService service =  proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "zhnsgan");
        System.out.println(service.hello(object));

        BookStore proxy1 = proxy.getProxy(BookStore.class);
        Book book = new Book("十万个为什么", 23.1);
        System.out.println(proxy1.saleBook(book));
    }
}
