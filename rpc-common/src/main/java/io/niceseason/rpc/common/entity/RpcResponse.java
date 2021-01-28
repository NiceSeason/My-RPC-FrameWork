package io.niceseason.rpc.common.entity;

import io.niceseason.rpc.common.enumeration.ResponseStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class RpcResponse<T> implements Serializable {

    private String requestId;

    private Integer code;

    private String msg;

    private T data;

    public static <T> RpcResponse<T> success(String requestId,T data) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setCode(ResponseStatus.SUCCESS.getCode());
        response.setMsg(ResponseStatus.SUCCESS.getMsg());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseStatus code, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setCode(code.getCode());
        response.setMsg(code.getMsg());
        return response;
    }
}
