package io.niceseason.rpc.core.registry;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    /**
     * 通过服务名获取服务地址
     * @param serviceName
     * @return
     */
    InetSocketAddress lookupService(String serviceName);
}
