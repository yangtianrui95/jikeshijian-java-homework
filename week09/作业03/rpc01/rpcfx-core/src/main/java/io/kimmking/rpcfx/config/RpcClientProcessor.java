package io.kimmking.rpcfx.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.lang.Nullable;

import java.beans.PropertyDescriptor;

@Slf4j
public class RpcClientProcessor implements InstantiationAwareBeanPostProcessor {

    @Nullable
    PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        return null;
    }

}
