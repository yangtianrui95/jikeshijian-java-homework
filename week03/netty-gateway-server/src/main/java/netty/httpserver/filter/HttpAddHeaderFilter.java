package netty.httpserver.filter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Slf4j
public class HttpAddHeaderFilter implements HttpFilter {

    @Override
    public Mono<FullHttpResponse> doFilter(FullHttpRequest request, FilterChain filterChain) throws Exception {
        log.info("HttpAddHeaderFilter dofilter");
        final Mono<FullHttpResponse> response = filterChain.doFilter(request);
        request.headers().add("HttpAddHeaderFilter-AddResponseHeader", "NettyGateway");
        return response.doOnNext(new Consumer<FullHttpResponse>() {
            @Override
            public void accept(FullHttpResponse response) {
                log.info("add custom header ");
                response.headers().add("HttpAddHeaderFilter-AddResponseHeader", "NettyGateway");
            }
        });
    }
}
