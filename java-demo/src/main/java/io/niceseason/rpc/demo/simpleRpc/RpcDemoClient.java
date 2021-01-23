package io.niceseason.rpc.demo.simpleRpc;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import io.niceseason.rpc.api.HelloObject;
import io.niceseason.rpc.api.HelloService;
import io.niceseason.rpc.demo.proxy.IProducer;
import io.niceseason.rpc.demo.proxy.Producer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

public class RpcDemoClient {
    public static void main(String[] args) {
        HelloService proxyInstance = (HelloService) Proxy.newProxyInstance(HelloService.class.getClassLoader(), new Class[]{HelloService.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                HelloObject object = (HelloObject) args[0];
                String response = sendRequest(object);
                return response;
            }
        });
        HelloObject object = new HelloObject(3, "TEST OBJECT");
        System.out.println(proxyInstance.hello(object));
    }

    private static String sendRequest(HelloObject object) {
        String result = "";
        try(Socket socket=new Socket("127.0.0.1",7);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
                out.writeObject(object);
                out.flush();
            result= (String) in.readObject();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
