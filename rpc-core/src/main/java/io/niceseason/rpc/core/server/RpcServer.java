package io.niceseason.rpc.core.server;

import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.core.client.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private ExecutorService executorService;

    public RpcServer() {
         executorService = Executors.newFixedThreadPool(10);
    }

    public void register(int port, Object service) {
        try(ServerSocket serverSocket=new ServerSocket(port)){
            logger.info("服务器正在启动...");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("接收到客户端请求:"+socket.getRemoteSocketAddress());
//                executorService.execute(new WorkThread(socket, service));
                try (
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ) {
                    RpcRequest request = (RpcRequest) in.readObject();
                    Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterType());
                    Object returnObject = method.invoke(service, request.getParameters());
                    RpcResponse<Object> success = RpcResponse.success(returnObject);
                    logger.info("获取到返回对象{}",success);
                    out.writeObject(success);
                    out.flush();
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                    logger.error("执行代理方法时有出错误::", e);
                }
            }
        } catch (IOException e) {
            logger.error("连接客户端时有错误发生:", e);
        }
    }
}
