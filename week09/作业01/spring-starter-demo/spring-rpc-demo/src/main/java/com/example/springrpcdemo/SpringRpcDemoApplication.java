package com.example.springrpcdemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringRpcDemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringRpcDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
