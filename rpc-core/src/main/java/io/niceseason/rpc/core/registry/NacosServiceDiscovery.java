package io.niceseason.rpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.niceseason.rpc.common.util.NacosUtils;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceDiscovery {

    private  final NamingService namingService;


    public NacosServiceDiscovery() {
        namingService = NacosUtils.getNamingService();
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        List<Instance> allInstances = NacosUtils.getAllInstances(namingService, serviceName);
        Instance instance = allInstances.get(0);
        return new InetSocketAddress(instance.getIp(), instance.getPort());
    }
}
