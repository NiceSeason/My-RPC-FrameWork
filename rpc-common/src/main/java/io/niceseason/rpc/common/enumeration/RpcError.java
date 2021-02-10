package io.niceseason.rpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcError {
    FAIDLED_TO_GET_SERVICE_INSTANCE("获取服务对象失败"),
    MAIN_CLASS_ABSENT_RPCSCAN("启动类未标识RpcScan注解"),
    FAILED_TO_GET_CHANNEL("chanel获取失败"),
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务端失败"),
    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),
    UNKNOWN_PROTOCOL("不识别的协议包"),
    UNKNOWN_SERIALIZER("不识别的(反)序列化器"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型"),
    RESPONSE_NOT_MATCH("响应与请求号不匹配"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接注册中心失败"),
    REGISTER_SERVICE_FAILED("注册服务失败");

    private final String message;

}
