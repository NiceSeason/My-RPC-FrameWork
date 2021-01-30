package io.niceseason.rpc.common.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import io.niceseason.rpc.common.enumeration.RpcError;
import io.niceseason.rpc.common.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosUtils {

    private static final Logger logger = LoggerFactory.getLogger(NacosUtils.class);

    private  static final String SERVER_ADDR = "127.0.0.1:8848";


    public static NamingService getNamingService() {
        try {
            NamingService namingService = new NacosNamingService(SERVER_ADDR);
            logger.info("成功连接到Nacos");
            return namingService;
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public static void registerService(NamingService namingService, String serviceName, InetSocketAddress address) {
        try {
            namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    public static List<Instance> getAllInstances(NamingService namingService,String serviceName) {
        try {
            List<Instance> allInstances = namingService.getAllInstances(serviceName);
            return allInstances;
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
            return null;
        }
    }

}
