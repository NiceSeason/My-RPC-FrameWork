package io.niceseason.rpc.core.server;

import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.core.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class RequestHandlerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);


    private Socket socket;

    private ServiceRegistry serviceRegistry;


    public RequestHandlerThread(Socket socket, ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
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
            Object service = serviceRegistry.getService(serviceName);
            RequestHandler requestHandler = new RequestHandler();
            Object returnObject=requestHandler.handler(request, service);
            out.writeObject(RpcResponse.success(returnObject));
            out.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用服务器对象时有错误产生", e);
        }
    }
}
