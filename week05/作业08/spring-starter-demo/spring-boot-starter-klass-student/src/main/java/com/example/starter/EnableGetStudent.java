package com.example.starter;

import com.example.starter.configuration.EnableGetStudentConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加上这个注解后表示可以获取StudentBean
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({EnableGetStudentConfiguration.class})
public @interface EnableGetStudent {
}
