package com.example.rpc.client;

import com.example.rpc.InvokeAction;
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
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;

@Slf4j
public class RpcClient {

    @SneakyThrows
    public Object sendInvocationForResult(@Nonnull InvokeAction invokeAction) {
        final RpcResultResolver rpcResultResolver = new RpcResultResolver();
        final Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,false)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        ch.pipeline().addLast(new LengthFieldPrepender(4));
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        ch.pipeline().addLast(rpcResultResolver);
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) {
                                log.info("channelactive");
                                ch.writeAndFlush(invokeAction);
                            }
                        });
                    }
                });
        final ChannelFuture channelFuture =
                bootstrap.connect(new InetSocketAddress("0.0.0.0", 9998))
                        .addListener((ChannelFutureListener) future -> log.info("operationComplete"));
        channelFuture.channel()
                .closeFuture()
                .sync();
        final Object result = rpcResultResolver.getInvocation().getResult();
        channelFuture.channel().close().sync();
        return result;
    }

    @Getter
    private static class RpcResultResolver extends SimpleChannelInboundHandler<InvokeAction> {
        private InvokeAction invocation;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, InvokeAction msg) throws Exception {
            invocation = msg;
        }
    }
}
