package io.niceseason.rpc.core.transport.netty.client;

import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRequests {

    private ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedRequestFutures = new ConcurrentHashMap<>();

    public void put(String requestId,CompletableFuture<RpcResponse> future) {
        unprocessedRequestFutures.put(requestId, future);
    }

    public void remove(String requestId) {
        unprocessedRequestFutures.remove(requestId);
    }

    public void complete(RpcResponse response) {
        CompletableFuture<RpcResponse> future = unprocessedRequestFutures.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        }else {
            throw new IllegalStateException("该处理请求不存在");
        }
    }
}
