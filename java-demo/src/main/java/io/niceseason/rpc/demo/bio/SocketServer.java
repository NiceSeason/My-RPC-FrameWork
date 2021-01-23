package io.niceseason.rpc.demo.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket=null;
        try {
            serverSocket = new ServerSocket(7);
        } catch (IOException e) {
            System.out.println("服务器监听端口失败");
        }
        try (
                Socket clientSocket = serverSocket.accept();
                BufferedWriter out =
                        new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                // 接收客户端的信息
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String str;
            while ((str=in.readLine())!=null){
               out.write(str);
               out.newLine();
               out.flush();
                System.out.println(
                        "BlockingEchoServer -> " + clientSocket.getRemoteSocketAddress() + ":" + str);
            }
        }
    }
}
