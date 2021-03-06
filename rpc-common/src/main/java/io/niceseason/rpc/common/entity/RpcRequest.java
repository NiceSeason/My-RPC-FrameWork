package io.niceseason.rpc.common.entity;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RpcRequest implements Serializable {

    /**
     * 请求号
     */
    private String requestId;

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

    /**
     * 是否是心跳包
     */
    private boolean heart;
}
