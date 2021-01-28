package io.niceseason.rpc.core.transport.socket.server;

import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.core.RequestHandler;
import io.niceseason.rpc.core.provider.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestHandlerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);


    private Socket socket;

    private ServiceProvider serviceProvider;


    public RequestHandlerThread(Socket socket, ServiceProvider serviceRegistry) {
        this.serviceProvider = serviceRegistry;
        this.socket = socket;
    }

    @Override
    public void run() {
        logger.info("工作线程开始执行任务");
        try (
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ) {
            RpcRequest request = (RpcRequest) in.readObject();
            String serviceName = request.getInterfaceName();
            Object service = serviceProvider.getProvider(serviceName);
            RequestHandler requestHandler = new RequestHandler();
            Object returnObject=requestHandler.handler(request, service);
            out.writeObject(RpcResponse.success(request.getRequestId(),returnObject));
            out.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用服务器对象时有错误产生", e);
        }
    }
}
