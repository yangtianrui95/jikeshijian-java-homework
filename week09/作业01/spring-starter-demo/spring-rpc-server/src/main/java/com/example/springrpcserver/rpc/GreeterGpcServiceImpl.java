package com.example.springrpcserver.rpc;

import com.example.GreeterGrpc;
import com.example.HelloProto;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GreeterGpcServiceImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloReply> responseObserver) {
        log.info("sayHello, request:{}", request.getName());
        responseObserver.onNext(HelloProto.HelloReply.newBuilder()
                .setMessage("From Service:" + request.getName())
                .build());
        responseObserver.onCompleted();
    }
}
