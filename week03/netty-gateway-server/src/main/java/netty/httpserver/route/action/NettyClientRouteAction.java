package netty.httpserver.route.action;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import netty.httpserver.route.HttpRouteFilter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
public class NettyClientRouteAction implements HttpRouteFilter.RouteAction {

    private final String mHost;
    private final int mPort;
    private final String mUri;

    public NettyClientRouteAction(String host, int port, String uri) {
        this.mPort = port;
        this.mHost = host;
        this.mUri = uri;
    }

    @Override
    public Mono<FullHttpResponse> routeTo(FullHttpRequest request) {
        return Mono.create(monoSink -> {
            final NioEventLoopGroup group = new NioEventLoopGroup();
            try {
                final Bootstrap bootstrap = new Bootstrap()
                        .group(group)
                        .channel(NioSocketChannel.class)
                        .remoteAddress(new InetSocketAddress(mHost, mPort))
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<Channel>() {
                            @Override
                            protected void initChannel(Channel ch) {
                                ch.pipeline().addLast(new HttpClientCodec());
                                ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024));
                                ch.pipeline().addLast(new HttpContentDecompressor());
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        final URI uri = new URI(NettyClientRouteAction.this.mUri);
                                        final DefaultFullHttpRequest httpGet = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                                                HttpMethod.GET, uri.toASCIIString());
                                        httpGet.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                                        httpGet.headers().add(HttpHeaderNames.CONTENT_LENGTH, httpGet.content().readableBytes());
                                        httpGet.headers().add(HttpHeaderNames.HOST, mHost + ":" + mPort);
                                        log.info("channelActive, {}", uri.toASCIIString());
                                        ctx.writeAndFlush(httpGet);
                                    }

                                    @Override
                                    public void channelReadComplete(ChannelHandlerContext ctx) {
                                        log.info("channelReadComplete");
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                        log.error("NettyClient: channelRead: {}", msg);
                                        final FullHttpResponse response = (FullHttpResponse) msg;
                                        monoSink.success(response);
                                        // close关闭连接
                                        ctx.close();
                                    }
                                });
                            }
                        });
                final ChannelFuture channelFuture = bootstrap.connect()
                        .sync();
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                catchException(e);
            } finally {
                group.shutdownGracefully();
            }
        });
    }


    private void catchException(Exception e) {
        throw new RuntimeException(e);
    }


}
