package io.niceseason.rpc.core.transport.socket.server;

import io.niceseason.rpc.core.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

//public class SocketServer implements RpcServer {
//
//    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
//
//    private ExecutorService executorService;
//
//    private ServiceRegistry serviceRegistry;
//
//    public SocketServer(ServiceRegistry serviceRegistry) {
//        this.serviceRegistry = serviceRegistry;
//        int coreSize = 10;
//        int maxSize =50;
//        long keepTime = 60;
//        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
//        ThreadFactory threadFactory = Executors.defaultThreadFactory();
//        executorService = new ThreadPoolExecutor(coreSize, maxSize, keepTime, TimeUnit.SECONDS, workQueue, threadFactory);
//    }
//
//    public void start(int port) {
//        try(ServerSocket serverSocket=new ServerSocket(port)){
//            logger.info("服务器正在启动...");
//            Socket socket;
//            while ((socket = serverSocket.accept()) != null) {
//                logger.info("接收到客户端请求:"+socket.getRemoteSocketAddress());
//                executorService.execute(new RequestHandlerThread(socket,serviceRegistry));
//            }
//        } catch (IOException e) {
//            logger.error("连接客户端时有错误发生:", e);
//        }
//    }
//}
