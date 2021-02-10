package io.niceseason.rpc.core.remoting.transport;

import io.niceseason.rpc.common.enumeration.RpcError;
import io.niceseason.rpc.common.exception.RpcException;
import io.niceseason.rpc.common.util.ReflectUtil;
import io.niceseason.rpc.core.annotation.RpcScan;
import io.niceseason.rpc.core.annotation.RpcService;
import io.niceseason.rpc.core.provider.ServiceProvider;
import io.niceseason.rpc.core.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;


@Slf4j
public abstract class AbstractRpcServer implements RpcServer {


    protected String host;

    protected int port;

    protected ServiceRegistry serviceRegistry;

    protected ServiceProvider serviceProvider;

    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> mainClass = null;
        try {
            mainClass = Class.forName(mainClassName);
            if (!mainClass.isAnnotationPresent(RpcScan.class)) {
                throw new RpcException(RpcError.MAIN_CLASS_ABSENT_RPCSCAN);
            }
        } catch (ClassNotFoundException e) {
            log.error("获取启动类产生错误:{}", e.getMessage());
        }
        //获取扫描路径
        String basePackage =mainClass.getAnnotation(RpcScan.class).value();
        if (basePackage.equals("")) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }

        Set<Class<?>> classes = ReflectUtil.getClasses(basePackage);
        for (Class<?> aClass : classes) {
            if (aClass.isAnnotationPresent(RpcService.class)) {
                String serviceName = aClass.getAnnotation(RpcService.class).name();
                Object service = null;
                try {
                    service = aClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("获取服务对象失败");
                    throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE);
                }
                if (serviceName.equals("")) {
                    for (Class<?> anInterface : aClass.getInterfaces()) {
                        publishService(service, anInterface.getCanonicalName());
                    }
                }else {
                    publishService(service, serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(Object service, String serviceName) {
        serviceProvider.addProvider(service);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
