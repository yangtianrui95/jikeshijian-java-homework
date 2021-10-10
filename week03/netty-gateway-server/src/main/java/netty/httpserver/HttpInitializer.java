package netty.httpserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import netty.httpserver.filter.HttpFilter;
import netty.httpserver.filter.HttpFilterInboundHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {

    private final List<HttpFilter> mHttpFilterList = new ArrayList<>();
	
	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		p.addLast(new HttpServerCodec());
		p.addLast(new HttpObjectAggregator(1024 * 1024));
		p.addLast(new HttpFilterInboundHandler(mHttpFilterList));
	}

	public void registerHttpFilter(HttpFilter filter) {
        Objects.requireNonNull(filter);
	    mHttpFilterList.add(filter);
    }
}
