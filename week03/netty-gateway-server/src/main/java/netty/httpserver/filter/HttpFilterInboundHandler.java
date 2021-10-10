package netty.httpserver.filter;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class HttpFilterInboundHandler extends ChannelInboundHandlerAdapter {
    private final List<HttpFilter> mHttpFilterList;

    public HttpFilterInboundHandler(List<HttpFilter> filter) {
        mHttpFilterList = new ArrayList<>(filter);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            final FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            log.info("channelRead:{} ", fullHttpRequest.getUri());
            final Mono<FullHttpResponse> responseMono = handleFilter(mHttpFilterList, 0, fullHttpRequest);
            responseMono/*.delayElement(Duration.ofMillis(5000))*/
                    .subscribe(response -> {
                log.info("write to resp: {}", response);
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            });
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private Mono<FullHttpResponse> handleFilter(List<HttpFilter> httpFilterList,
                                               int currentFilterIndex,
                                               FullHttpRequest request)  throws Exception{
        if (currentFilterIndex < 0 || currentFilterIndex > httpFilterList.size() - 1) {
            return Mono.empty();
        }
        final HttpFilter httpFilter = httpFilterList.get(currentFilterIndex);
        log.info("filterType: {}", httpFilter.getClass().getName());
        Objects.requireNonNull(httpFilter);
        return httpFilter.doFilter(request, (req) -> {
            try {
                return handleFilter(httpFilterList, currentFilterIndex + 1, req);
            } catch (Exception e) {
                catchException(e);
            }
            return Mono.empty();
        });
    }

    private void catchException(Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("channelReadComplete");
    }
}
