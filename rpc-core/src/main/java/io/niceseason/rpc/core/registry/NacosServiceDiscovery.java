package io.niceseason.rpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.niceseason.rpc.common.enumeration.RpcError;
import io.niceseason.rpc.common.exception.RpcException;
import io.niceseason.rpc.common.util.NacosUtils;
import io.niceseason.rpc.core.loadbalancer.LoadBalancer;
import io.niceseason.rpc.core.loadbalancer.RoundRobinLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceDiscovery {

    private final static Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private LoadBalancer loadBalancer;

    public NacosServiceDiscovery() {
        this.loadBalancer = new RoundRobinLoadBalancer();
    }

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        List<Instance> instances = NacosUtils.getAllInstances(serviceName);
        if (instances==null||instances.size()==0) throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        Instance instance = loadBalancer.select(instances);
        return new InetSocketAddress(instance.getIp(), instance.getPort());
    }
}
