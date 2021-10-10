package netty.httpserver.route.action;

import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import netty.httpserver.route.HttpRouteFilter;
import reactor.core.publisher.Mono;

public class FileResultRouteAction implements HttpRouteFilter.RouteAction {
    @Override
    public Mono<FullHttpResponse> routeTo(FullHttpRequest request) {
        return null;
    }
}
