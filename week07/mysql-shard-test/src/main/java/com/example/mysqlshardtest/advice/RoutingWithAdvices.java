package com.example.mysqlshardtest.advice;

import com.example.mysqlshardtest.config.RoutingDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
public class RoutingWithAdvices {

//    @Pointcut(value = "@annotation(com.example.mysqlshardtest.advice.RoutingWith)")
//    public void pointcut() {
//
//    }

    @Before("@annotation(RoutingWith)")
    public void around(JoinPoint joinPoint) {
        log.error("aop!!!!!!!!!");
        final String dataSourceKey = ((MethodSignature) joinPoint.getSignature())
                .getMethod().getAnnotation(RoutingWith.class).name();
        RoutingDataSource.Context.setSourceKey(dataSourceKey);
        RoutingDataSource.Context.remove();
    }

}
