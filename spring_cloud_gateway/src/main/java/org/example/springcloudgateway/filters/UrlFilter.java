package org.example.springcloudgateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class UrlFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println("执行第二个Filter:UrlFilter");
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();

        String path = uri.getPath();

        System.out.println("Uri : "+path);
        return chain.filter(exchange);//如果这里return null，会抛异常The Mono returned by the supplier is null
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
