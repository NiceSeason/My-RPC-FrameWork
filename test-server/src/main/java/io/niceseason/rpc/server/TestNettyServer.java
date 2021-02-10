package io.niceseason.rpc.server;

import io.niceseason.rpc.api.HelloService;
import io.niceseason.rpc.core.annotation.RpcScan;
import io.niceseason.rpc.core.remoting.transport.RpcServer;
import io.niceseason.rpc.core.remoting.transport.netty.server.NettyServer;

@RpcScan
public class TestNettyServer {
    public static void main(String[] args) {
        RpcServer rpcServer = new NettyServer("127.0.0.1", 7);
        rpcServer.start();
    }
}
