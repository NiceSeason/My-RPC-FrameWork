package io.niceseason.rpc.core.server;

import io.niceseason.rpc.common.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public Object handler(RpcRequest request, Object service) {
        Object object = null;
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterType());
            object = method.invoke(service, request.getParameters());
            logger.info("服务:{} 成功调用方法:{}", request.getInterfaceName(), request.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("方法执行产生错误：", e);
        }
        return object;
    }
}
