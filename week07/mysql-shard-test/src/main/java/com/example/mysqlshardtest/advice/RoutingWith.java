package com.example.mysqlshardtest.advice;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoutingWith {
    String name() default "masterDataSource";
}
