package io.niceseason.rpc.demo.simpleRpc;

import io.niceseason.rpc.api.HelloObject;
import io.niceseason.rpc.api.HelloService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcDemoServer {
    public static void main(String[] args) {
        HelloService service = new HelloServiceImpl();
        try(ServerSocket serverSocket=new ServerSocket(7)) {
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    HelloObject readObject = (HelloObject) in.readObject();
                    System.out.println("服务端接收到对象:"+readObject);
                    String hello = service.hello(readObject);
                    out.writeObject(hello);
                    out.flush();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {


        }
    }
}
