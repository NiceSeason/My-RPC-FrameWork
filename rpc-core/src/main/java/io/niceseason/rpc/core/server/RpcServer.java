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
import java.util.concurrent.*;

public class RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private ExecutorService executorService;

    public RpcServer() {
        int coreSize = 10;
        int maxSize =50;
        long keepTime = 60;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executorService = new ThreadPoolExecutor(coreSize, maxSize, keepTime, TimeUnit.SECONDS, workQueue, threadFactory);
    }

    public void register(int port, Object service) {
        try(ServerSocket serverSocket=new ServerSocket(port)){
            logger.info("服务器正在启动...");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("接收到客户端请求:"+socket.getRemoteSocketAddress());
                executorService.execute(new WorkThread(socket, service));
            }
        } catch (IOException e) {
            logger.error("连接客户端时有错误发生:", e);
        }
    }
}
