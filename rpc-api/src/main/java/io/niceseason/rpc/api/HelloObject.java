package io.niceseason.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 测试用api的实体
 * @author ziyang
 */
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelloObject implements Serializable {

    private Integer id;
    private String message;

}
