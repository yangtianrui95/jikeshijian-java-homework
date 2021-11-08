package com.example.mysqlshardtest.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.example.mysqlshardtest.entity")
public class MybatisConfiguration {
}
