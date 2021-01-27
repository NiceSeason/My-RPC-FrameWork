package io.niceseason.rpc.core.provider;

import javax.imageio.spi.ServiceRegistry;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceProvider implements ServiceProvider {

    /**
     * 注册表
     * 存储注册服务和对应的对象
     */
    public static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * 存储已经注册的服务对象
     */
    public static final Set<String> registeredService = ConcurrentHashMap.newKeySet();



    @Override
    public synchronized <T> void addProvider(T service) {
        if (registeredService.contains(service.getClass().getCanonicalName())) return;
        registeredService.add(service.getClass().getCanonicalName());
        for (Class<?> anInterface : service.getClass().getInterfaces()) {
            serviceMap.put(anInterface.getCanonicalName(), service);
        }
    }

    @Override
    public Object getProvider(String serviceName) {
        return serviceMap.get(serviceName);
    }
}
