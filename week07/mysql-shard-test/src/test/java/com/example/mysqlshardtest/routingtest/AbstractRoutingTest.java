package com.example.mysqlshardtest.routingtest;

import com.example.mysqlshardtest.MysqlShardTestApplicationTests;
import com.example.mysqlshardtest.advice.RoutingWithAdvices;
import com.example.mysqlshardtest.advice.RoutingWith;
import com.example.mysqlshardtest.config.RoutingDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

@Slf4j
public class AbstractRoutingTest extends MysqlShardTestApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ApplicationContext context;

    @Test
    @RoutingWith(name = "masterDataSource")
    @SneakyThrows
    public void masterDataSourceTest() {
        final String url = dataSource.getConnection().getMetaData().getURL();
        log.error("url: {}", url);
        Assertions.assertThat(url).contains("jdbc:mysql://localhost:3307/commerce");
    }

    @Test
    @RoutingWith(name = "slaveDataSource")
    @SneakyThrows
    public void slaveDataSourceTest() {
        RoutingDataSource.Context.setSourceKey("slaveDataSource");
        final String url = dataSource.getConnection().getMetaData().getURL();
        log.error("url: {}", url);
        log.error("key: , {}", context.getBean(RoutingWithAdvices.class));
    }
}
