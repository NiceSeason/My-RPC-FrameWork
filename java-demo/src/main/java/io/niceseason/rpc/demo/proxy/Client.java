package io.niceseason.rpc.demo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Client {
    public static void main(String[] args) {
        Producer producer = new Producer();
        IProducer proxyInstance = (IProducer) Proxy.newProxyInstance(producer.getClass().getClassLoader(), producer.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                float money = (float) args[0];
                if (method.getName().equals("sale")) {
                    System.out.println("方法被增强");
                    money = money * 5;
                }
                return method.invoke(producer, money);
            }
        });
        proxyInstance.sale(100);
        producer.sale(100);
    }
}
