package io.niceseason.rpc.core.remoting.transport;


public interface RpcServer {

    void start();

    <T> void publishService(Object service, String serviceName);
}
