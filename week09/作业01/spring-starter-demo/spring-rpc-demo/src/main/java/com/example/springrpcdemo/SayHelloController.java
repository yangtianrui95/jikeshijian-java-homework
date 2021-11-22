package com.example.springrpcdemo;

import com.example.GreeterGrpc;
import com.example.HelloProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;

@RestController
public class SayHelloController {

    @GetMapping("/sayHello")
    public String sayHello(@Nonnull @RequestParam String msg) {
        final ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("rpc-server", 9991)
                .usePlaintext()
                .build();
        final HelloProto.HelloReply helloReply = GreeterGrpc.newBlockingStub(managedChannel)
                .sayHello(HelloProto.HelloRequest.newBuilder()
                        .setName(msg)
                        .build());
        return helloReply.getMessage();
    }
}
