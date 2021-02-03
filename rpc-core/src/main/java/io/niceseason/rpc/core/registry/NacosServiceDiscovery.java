package io.niceseason.rpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.niceseason.rpc.common.enumeration.RpcError;
import io.niceseason.rpc.common.exception.RpcException;
import io.niceseason.rpc.common.util.NacosUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceDiscovery {
    private final static Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private  final NamingService namingService;


    public NacosServiceDiscovery() {
        namingService = NacosUtils.getNamingService();
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        List<Instance> instances = NacosUtils.getAllInstances(namingService, serviceName);
        if (instances==null||instances.size()==0) throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        Instance instance = instances.get(0);
        return new InetSocketAddress(instance.getIp(), instance.getPort());
    }
}
