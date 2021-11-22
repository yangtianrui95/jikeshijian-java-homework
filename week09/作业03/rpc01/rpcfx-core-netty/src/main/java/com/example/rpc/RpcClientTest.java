package com.example.rpc;

import com.example.rpc.client.Rpcfx;
import com.example.rpc.test.IMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class RpcClientTest {
    public static void main(String[] args) throws Exception {
        final Object result = Rpcfx.create(IMessage.class).sendMessage("haha");
        log.info("result: {}", result);

        sendTest();
    }

    private static void sendTest() {
        final Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4,0,4));
                        ch.pipeline().addLast(new LengthFieldPrepender(4));
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                log.info("channelactive");
                                final InvokeAction action = InvokeAction.builder()
                                        .clazz("bb")
                                        .arguments(new String[] {"aa"})
                                        .build();
                                ch.writeAndFlush(action);
                            }

                        });
                    }
                });
        final ChannelFuture channelFuture =
                bootstrap.connect(new InetSocketAddress("0.0.0.0", 9998))
                .addListener((ChannelFutureListener) future -> log.info("operationComplete"));
    }
}
