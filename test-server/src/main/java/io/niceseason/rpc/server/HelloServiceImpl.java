package io.niceseason.rpc.server;

import io.niceseason.rpc.api.HelloObject;
import io.niceseason.rpc.api.HelloService;
import io.niceseason.rpc.core.annotation.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RpcService
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("HelloServiceImpl接收到：{}", object.getMessage());
        return object.getId() + "---" + object.getMessage();
    }
}
