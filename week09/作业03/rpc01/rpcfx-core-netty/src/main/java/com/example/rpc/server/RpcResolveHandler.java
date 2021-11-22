package com.example.rpc.server;

import com.example.rpc.InvokeAction;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class RpcResolveHandler extends SimpleChannelInboundHandler<InvokeAction> {

    private final ConcurrentHashMap<Class<?>, Object> mRegistryObject = new ConcurrentHashMap<>();

    public <T> void registerRpcService(Class<T> clazz, T service) {
        mRegistryObject.put(clazz, service);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InvokeAction msg) throws Exception {
        log.info("channelRead0:{}", msg);
        final String clazz = msg.getClazz();
        final Class<?> clazzClass = Class.forName(clazz);
        final Object registry = mRegistryObject.get(clazzClass);
        if (registry != null) {
            final Object result = clazzClass.getDeclaredMethod(msg.getMethodName(), msg.getArgumentsTypes())
                    .invoke(registry, msg.getArguments());
            ctx.writeAndFlush(msg
                    .toBuilder()
                    .result(result)
                    .build())
                    .sync();
            ctx.close();
        }
    }
}
