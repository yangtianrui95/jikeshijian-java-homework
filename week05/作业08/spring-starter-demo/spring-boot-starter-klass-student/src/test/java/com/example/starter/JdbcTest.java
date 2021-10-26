package com.example.starter;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@Slf4j
//https://www.liaoxuefeng.com/wiki/1252599548343744/1321748500840481
public class JdbcTest {

    protected final Connection connection;

    @SneakyThrows
    public JdbcTest() {
        final Properties properties = new Properties();
        properties.put("user", "root");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/world", properties);
        log.info("connectionaaa: {}", connection);
    }
}
