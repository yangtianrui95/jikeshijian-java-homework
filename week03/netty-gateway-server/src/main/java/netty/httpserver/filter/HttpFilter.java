package netty.httpserver.filter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import reactor.core.publisher.Mono;

public interface HttpFilter {

     Mono<FullHttpResponse> doFilter(final FullHttpRequest request , final FilterChain filterChain) throws Exception;

    interface FilterChain {
        Mono<FullHttpResponse> doFilter(FullHttpRequest request);
    }
}
