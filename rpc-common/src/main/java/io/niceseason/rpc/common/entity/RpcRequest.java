package io.niceseason.rpc.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Builder
@Getter
public class RpcRequest implements Serializable {

    /**
     * 调用接口名
     */
    private String interfaceName;

    /**
     * 调用方法名
     */
    private String methodName;

    /**
     * 方法参数类型(用于确定重载的方法)
     */
    private Class<?>[] parameterType;

    /**
     * 方法参数
     */
    private Object[] parameters;
}
