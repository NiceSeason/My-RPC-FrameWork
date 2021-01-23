package io.niceseason.rpc.demo.bio;

import io.niceseason.rpc.api.HelloObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketObjectClient {
    public static void main(String[] args) {
        try
         {
            Socket socket = new Socket("127.0.0.1", 7);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            HelloObject helloObject = new HelloObject(19, "init...");
            out.writeObject(helloObject);
            out.flush();
            Object readObject = in.readObject();
            System.out.println(readObject);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
