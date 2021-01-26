package io.niceseason.rpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerCode {
    JSON(1);

    private final int code;
}
