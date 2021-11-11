package com.example.mysqlshardtest.config;

import org.mybatis.spring.annotation.MapperScan;

//@Configuration
@MapperScan(basePackages = "com.example.mysqlshardtest.entity")
public class MybatisConfiguration {

}
