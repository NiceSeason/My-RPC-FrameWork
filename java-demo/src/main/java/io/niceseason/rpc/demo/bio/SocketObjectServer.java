package io.niceseason.rpc.demo.bio;

import io.niceseason.rpc.api.HelloObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * bio传输对象server
 */
public class SocketObjectServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(7)) {
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    HelloObject readObject = (HelloObject) in.readObject();
                    readObject.setId(199);
                    readObject.setMessage("after alter...");
                    out.writeObject(readObject);
                    out.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
