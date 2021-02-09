package io.niceseason.rpc.demo.singleton;

/**
 * 单例饿汉模式
 */
public class SingleTonInstance {

    //volatile添加内存屏障，在instance被写后才能被读
    private static volatile SingleTonInstance instance=null;

    private SingleTonInstance() {
    }

    public static SingleTonInstance getSingleTonInstance() {
        //双重检查，注重效率的同时避免多线程的问题
        if (instance == null) {
            synchronized (SingleTonInstance.class) {
                if (instance == null) {
                    instance = new SingleTonInstance();
                }
            }
        }
        return instance;
    }
}
