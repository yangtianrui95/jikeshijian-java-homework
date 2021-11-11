package com.example.mysqlshardtest;

import com.example.mysqlshardtest.advice.RoutingWith;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MysqlShardTestApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MysqlShardTestApplication.class, args);
    }

    @RoutingWith
    public void test() {
        log.error("test xxx: ");
    }

    @Override
    public void run(String... args) throws Exception {
        test();
    }
}
