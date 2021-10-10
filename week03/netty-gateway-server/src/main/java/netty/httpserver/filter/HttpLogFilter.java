package netty.httpserver.filter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class HttpLogFilter implements HttpFilter {

    @Override
    public Mono<FullHttpResponse> doFilter(FullHttpRequest request, FilterChain filterChain) {
        log.info("HttpLogRequest: uri {}", request.getUri());
        return filterChain.doFilter(request);
    }
}
