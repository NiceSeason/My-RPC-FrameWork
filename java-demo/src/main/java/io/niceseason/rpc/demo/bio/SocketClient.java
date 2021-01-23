package io.niceseason.rpc.demo.bio;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    public static void main(String[] args) {
        Socket socket = null;


        try (
                Socket echoSocket = new Socket("127.0.0.1", 7);
                BufferedWriter out =
                        new BufferedWriter(new OutputStreamWriter(echoSocket.getOutputStream()));
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String string;
            while ((string=stdIn.readLine())!= null) {
               out.write(string);
               out.newLine();
                out.flush();
                System.out.println("echo:"+in.readLine());
            }
        }catch (Exception e){
            System.out.println("处理字符");
        }
    }
}
