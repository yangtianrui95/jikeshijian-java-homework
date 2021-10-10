package netty.httpserver.route.action;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import netty.httpserver.route.HttpRouteFilter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class HttpClientNIORouteAction implements HttpRouteFilter.RouteAction {

    private final CloseableHttpAsyncClient httpclient;

    public HttpClientNIORouteAction() {
        int cores = Runtime.getRuntime().availableProcessors();
        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();
        httpclient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((response, context) -> 6000)
                .build();
        httpclient.start();
    }

    private static Mono<FullHttpResponse> mappingToFullResponse(HttpResponse httpResponse) {
        try {
            byte[] body = EntityUtils.toByteArray(httpResponse.getEntity());
            final DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(body));
            return Mono.just(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<FullHttpResponse> routeTo(FullHttpRequest request) {
        return Mono.create((Consumer<MonoSink<HttpResponse>>) monoSink -> {
            log.info("start use httpclient ");
            final String uri = findMappingUrl(request);
            final HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
            httpclient.execute(httpGet, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(HttpResponse httpResponse) {
                    log.info("completed, {}", httpResponse);
                    monoSink.success(httpResponse);
                }

                @Override
                public void failed(Exception e) {
                    log.error("failed", e);
                    monoSink.error(e);
                }

                @Override
                public void cancelled() {
                    log.error("cancelled");
                }
            });
        }).subscribeOn(Schedulers.parallel())
                .flatMap((Function<HttpResponse, Mono<FullHttpResponse>>)
                        HttpClientNIORouteAction::mappingToFullResponse);
    }

    private String findMappingUrl(FullHttpRequest request) {
        return "https://www.baidu.com";
    }
}