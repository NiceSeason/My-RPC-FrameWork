package io.niceseason.rpc.core;

import io.niceseason.rpc.common.entity.RpcRequest;
import io.niceseason.rpc.common.entity.RpcResponse;
import io.niceseason.rpc.common.enumeration.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public Object handler(RpcRequest request, Object service) {
        Object object = null;
        try {
            object = invokeTargetMethod(request, service);

            logger.info("服务:{} 成功调用方法:{}", request.getInterfaceName(), request.getMethodName());
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("方法执行产生错误：", e);
        }
        return object;
    }

    private Object invokeTargetMethod(RpcRequest request, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method = null;
        try {
            method = service.getClass().getMethod(request.getMethodName(), request.getParameterType());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseStatus.NOT_FOUND_METHOD, request.getRequestId());
        }
       return method.invoke(service, request.getParameters());
    }
}
