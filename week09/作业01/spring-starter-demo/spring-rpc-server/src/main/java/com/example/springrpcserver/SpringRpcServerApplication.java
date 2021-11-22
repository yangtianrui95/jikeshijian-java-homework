package com.example.springrpcserver;

import com.example.springrpcserver.rpc.GreeterGpcServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringRpcServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringRpcServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final Server server = ServerBuilder.forPort(9991)
                .addService(new GreeterGpcServiceImpl())
                .build()
                .start();
        server.awaitTermination();
    }
}
