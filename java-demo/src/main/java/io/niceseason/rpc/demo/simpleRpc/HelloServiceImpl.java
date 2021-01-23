package io.niceseason.rpc.demo.simpleRpc;

import io.niceseason.rpc.api.HelloObject;
import io.niceseason.rpc.api.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(HelloObject object) {
        return object.getId() + "-------" + object.getMessage();
    }
}
