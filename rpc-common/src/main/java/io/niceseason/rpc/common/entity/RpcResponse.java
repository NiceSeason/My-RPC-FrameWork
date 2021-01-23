package io.niceseason.rpc.common.entity;

import io.niceseason.rpc.common.enumeration.ResponseStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse<T> implements Serializable {

    private Integer code;

    private String msg;

    private T data;

    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(ResponseStatus.SUCCESS.getCode());
        response.setMsg(ResponseStatus.SUCCESS.getMsg());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseStatus code) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(code.getCode());
        response.setMsg(code.getMsg());
        return response;
    }
}
