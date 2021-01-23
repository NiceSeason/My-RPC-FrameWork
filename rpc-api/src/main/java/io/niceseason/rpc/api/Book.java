package io.niceseason.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class Book implements Serializable {
    private String bookName;

    private Double price;
}
