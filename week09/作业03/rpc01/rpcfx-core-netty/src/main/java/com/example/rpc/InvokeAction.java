package com.example.rpc;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
@Builder(toBuilder = true)
public class InvokeAction implements Serializable {
    private String clazz;
    private String methodName;
    private Object[] arguments;
    private Class<?>[] argumentsTypes;
    private Object result;
}
