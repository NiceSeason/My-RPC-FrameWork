package io.niceseason.rpc.core.registry;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface ServiceRegistry {

    /**
     * 根据服务名注册服务
     * @param serviceName
     * @param address
     */
    void register(String serviceName, InetSocketAddress address);

    /**
     * 通过服务名获取服务地址
     * @param serviceName
     * @return
     */
    InetSocketAddress lookupService(String serviceName);

}
