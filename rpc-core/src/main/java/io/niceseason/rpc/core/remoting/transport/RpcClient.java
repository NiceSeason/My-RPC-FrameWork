package io.niceseason.rpc.core.remoting.transport;

import io.niceseason.rpc.common.entity.RpcRequest;


public interface RpcClient {
    Object sendRequest(RpcRequest request);
}
