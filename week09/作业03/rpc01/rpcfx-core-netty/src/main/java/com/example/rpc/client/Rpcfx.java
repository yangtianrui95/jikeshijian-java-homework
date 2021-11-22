package com.example.rpc.client;

import com.example.rpc.InvokeAction;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

@Slf4j
public class Rpcfx {
    public static <T> T create(Class<T> clazz) {
        final Class<?>[] interfaceClazz = clazz.isInterface() ? new Class[]{clazz} : clazz.getInterfaces();
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaceClazz, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                log.info("invoke, method: {}, args: {}", method, Arrays.toString(args));
                if (method.getDeclaringClass().equals(Object.class)) {
                    return method.invoke(proxy, args);
                }
                return rpcInvoke(method, args);
            }
        });
    }

    private static Object rpcInvoke(Method method, Object[] args) {
        return new RpcClient().sendInvocationForResult(InvokeAction.builder()
                .clazz(method.getDeclaringClass().getName())
                .argumentsTypes(method.getParameterTypes())
                .methodName(method.getName())
                .arguments(args)
                .build());
    }

}
