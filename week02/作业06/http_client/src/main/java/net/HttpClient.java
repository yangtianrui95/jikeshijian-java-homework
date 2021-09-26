package net;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Slf4j
public class HttpClient {

    public static final String TARGET = "http://localhost:9999";

    /*
    public static void main(String[] args) throws Exception {
        okHttpClientRequest();
        httpClientRequest();
    }
     */

    public static void httpClientRequest() throws Exception{
        final CloseableHttpClient client = HttpClients.createDefault();
        final HttpGet httpGet = new HttpGet(TARGET);
        final CloseableHttpResponse response = client.execute(httpGet);
        final String respString = EntityUtils.toString(response.getEntity(), "UTF-8");
        log.info("httpclient resp: {}", respString);
    }

    public static void okHttpClientRequest() throws IOException {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        final Request request = new Request.Builder()
                .url(TARGET)
                .build();
        final Call call = okHttpClient.newCall(request);
        final Response resp = call.execute();
        log.info("okhttp:result: {}", resp.body().string());
    }
}
