package netty.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import netty.httpserver.filter.HttpAddHeaderFilter;
import netty.httpserver.filter.HttpLogFilter;
import netty.httpserver.route.HttpRouteFilter;
import netty.httpserver.route.action.HttpClientNIORouteAction;
import netty.httpserver.route.action.LocalResultRouteAction;
import netty.httpserver.route.action.NettyClientRouteAction;

public class NettyHttpServer {

    public static final int PORT = 8808;

    public static void main(String[] args) throws InterruptedException {

        final int port = PORT;
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);
        try {
            ServerBootstrap b = new ServerBootstrap()
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .childOption(EpollChannelOption.SO_REUSEPORT, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            final HttpInitializer httpInitializer = new HttpInitializer();
            // #########################添加过滤器#########################
            // 打印Log的过滤器
            httpInitializer.registerHttpFilter(new HttpLogFilter());
            // request/response添加Header的过滤器
            httpInitializer.registerHttpFilter(new HttpAddHeaderFilter());
            // 路由配置过滤器
            httpInitializer.registerHttpFilter(new HttpRouteFilter() {{
                // 请求hello 接口直接返回hello-content字符串
                registerRouteUrl("/hello", new LocalResultRouteAction<>("hello-content"));
                // proxy/baidu 使用HttpClient代理到百度
                registerRouteUrl("/httpProxy/baidu", new HttpClientNIORouteAction());
                // proxy/sina 使用NettyClient代理到gateway-server.jar
                registerRouteUrl("/nettyProxy/gateway", new NettyClientRouteAction("82.157.105.67",8088,"/api/hello"));
            }});
            // #########################添加过滤器#########################

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(httpInitializer);
            Channel ch = b.bind(port).sync().channel();
            System.out.println("开启netty http服务器，监听地址和端口为 http://127.0.0.1:" + port + '/');
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
