package io.niceseason.rpc.core;


import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

//    private String host;
//
//    private int port;

    private RpcClient rpcClient;

    public RpcClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
//        this.host = host;
//        this.port = port;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> tClass) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, this);
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("代理对象的方法开始执行...");
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterType(method.getParameterTypes())
                .parameters(args)
                .build();
        RpcResponse response = (RpcResponse) rpcClient.sendRequest(request);
        return response.getData();
    }
}
