package io.niceseason.rpc.core.server;

import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class WorkThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(WorkThread.class);


    private Socket socket;

    private Object service;

    public WorkThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        logger.info("工作线程开始执行任务");
        try (
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ) {
            RpcRequest request = (RpcRequest) in.readObject();
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterType());
            Object returnObject = method.invoke(service, request.getParameters());
            out.writeObject(RpcResponse.success(returnObject));
            out.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("调用服务器对象时有错误产生", e);
        }
    }
}
