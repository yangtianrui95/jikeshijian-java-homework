package netty.httpserver.route.action;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import netty.httpserver.route.HttpRouteFilter;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
public class LocalResultRouteAction<T> implements HttpRouteFilter.RouteAction {

    private final T data;

    public LocalResultRouteAction(T data) {
        this.data = data;
    }

    @Override
    public Mono<FullHttpResponse> routeTo(FullHttpRequest request) {
        log.info("routeTo:{}", request.getUri());
        final byte[] payload = data.toString().getBytes(StandardCharsets.UTF_8);
        return Mono.just(
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(payload))
        );
    }
}