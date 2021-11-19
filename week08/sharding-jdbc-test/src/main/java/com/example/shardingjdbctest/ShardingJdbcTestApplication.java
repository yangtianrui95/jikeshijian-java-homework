package com.example.shardingjdbctest;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

/**
 * https://shardingsphere.apache.org/document/legacy/3.x/document/cn/quick-start/sharding-jdbc-quick-start/
 * https://www.jianshu.com/p/c3d2856e6e68
 */
@Slf4j
@SpringBootApplication
public class ShardingJdbcTestApplication implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(ShardingJdbcTestApplication.class, args);
    }

    @Override
    @SneakyThrows
    public void run(String... args) {
        for (int i = 0; i < 10; i++) {
            log.info("datasource {}",  dataSource.getConnection().getMetaData().getURL());
            log.info("datasource: {}", dataSource.getClass().getName());
        }
    }
}
