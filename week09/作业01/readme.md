
grpc client端

```java

        final ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("rpc-server", 9991)
                .usePlaintext()
                .build();
        final HelloProto.HelloReply helloReply = GreeterGrpc.newBlockingStub(managedChannel)
                .sayHello(HelloProto.HelloRequest.newBuilder()
                        .setName(msg)
                        .build());
        return helloReply.getMessage();
```

grpc server端

```java
        final Server server = ServerBuilder.forPort(9991)
                .addService(new GreeterGpcServiceImpl())
                .build()
                .start();
        server.awaitTermination();

```