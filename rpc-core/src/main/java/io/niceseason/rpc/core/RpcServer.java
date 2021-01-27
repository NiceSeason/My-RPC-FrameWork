package io.niceseason.rpc.core;


public interface RpcServer {

    void start();

    void publishService(Object service, Class<?> clazz);


}
