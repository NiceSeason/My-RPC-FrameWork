package io.niceseason.rpc.core.provider;

public interface ServiceProvider {

    /**
     * 将一个服务对象注册进注册表
     * @param service
     * @param <T>
     */
    <T> void addProvider(T service);

    /**
     * 通过服务名获得实体对象
     * @param serviceName
     * @return
     */
    Object getProvider(String serviceName);
}
