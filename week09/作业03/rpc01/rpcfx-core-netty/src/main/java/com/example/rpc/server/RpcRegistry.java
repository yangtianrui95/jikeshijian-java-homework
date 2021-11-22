package com.example.rpc.server;

import com.example.rpc.test.IMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
public class RpcRegistry {

    private static final int LENGTH_FIELD_LENGTH = 4;

    public static void main(String[] args) throws Exception{
        final RpcResolveHandler handler = new RpcResolveHandler();
        handler.registerRpcService(IMessage.class, msg -> "ok in service: " + msg);
        final ChannelFuture closeFuture = new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(16))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast("logger",new LoggingHandler());
                        ch.pipeline().addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, LENGTH_FIELD_LENGTH, 0, 4));
                        ch.pipeline().addLast("encoder",new LengthFieldPrepender(LENGTH_FIELD_LENGTH));
                        ch.pipeline().addLast("objectEncoder", new ObjectEncoder());
                        ch.pipeline().addLast("objectDecoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        ch.pipeline().addLast("rpcResolver", handler);
                    }
                })
                .bind(new InetSocketAddress(9998))
                .channel()
                .closeFuture()
                .sync();
    }
}
