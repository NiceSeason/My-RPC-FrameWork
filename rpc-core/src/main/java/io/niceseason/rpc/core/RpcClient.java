package io.niceseason.rpc.core;

import io.niceseason.rpc.common.entity.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public interface RpcClient {
    Object sendRequest(RpcRequest request);
}
