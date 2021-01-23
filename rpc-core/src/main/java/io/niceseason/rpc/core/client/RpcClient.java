package io.niceseason.rpc.core.client;


import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    public Object sendRequest(RpcRequest request, String host, int port) {
        Object response = null;
        try (
                Socket socket = new Socket(host, port);
        ) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(request);
            out.flush();
            response =  in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("发送请求时有错误产生", e);
        }
        return response;
    }

}
