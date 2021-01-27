package io.niceseason.rpc.core;

import io.niceseason.rpc.common.entity.RpcRequest;



public interface RpcClient {
    Object sendRequest(RpcRequest request);
}
