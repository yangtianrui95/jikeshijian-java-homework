package netty.httpserver.route;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import netty.httpserver.filter.HttpFilter;
import netty.httpserver.route.action.LocalResultRouteAction;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Slf4j
public class HttpRouteFilter implements HttpFilter {

    private final HashMap<String, RouteAction> mActionHashMap = new HashMap<>();

    @Override
    public Mono<FullHttpResponse> doFilter(FullHttpRequest request, FilterChain filterChain) {
        log.info("HttpRouteFilter uri: {} ", request.getUri());
        final RouteAction routeAction = mActionHashMap
                .getOrDefault(request.getUri(), new LocalResultRouteAction<>("NettyGateway-404"));
        return routeAction.routeTo(request);
    }

    public HttpRouteFilter registerRouteUrl(String url, RouteAction action) {
        mActionHashMap.put(url, action);
        return this;
    }

    public interface RouteAction {
        Mono<FullHttpResponse> routeTo(FullHttpRequest request);
    }
}
