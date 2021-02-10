package io.niceseason.rpc.core.remoting.transport.socket.client;


import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.core.remoting.transport.RpcClient;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@AllArgsConstructor
public class SocketClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private String host;

    private int port;

    @Override
    public Object sendRequest(RpcRequest request) {
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
