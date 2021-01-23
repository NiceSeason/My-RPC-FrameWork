package io.niceseason.rpc.client;

import io.niceseason.rpc.api.HelloObject;
import io.niceseason.rpc.api.HelloService;
import io.niceseason.rpc.core.client.RpcClientProxy;

public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 7);
        HelloService service =  proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "zhnsgan");
        System.out.println(service.hello(object));
    }
}
