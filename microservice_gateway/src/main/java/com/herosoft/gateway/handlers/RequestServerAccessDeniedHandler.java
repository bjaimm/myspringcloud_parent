package com.herosoft.gateway.handlers;

import com.alibaba.fastjson2.JSON;
import com.herosoft.commons.results.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

@Component
@Slf4j
public class RequestServerAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {
        log.info("开始处理鉴权结果是拒绝的情况。。。");
        ServerHttpResponse response=exchange.getResponse();

        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String responseBody = JSON.toJSONString(Result.otherError(ex));

        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(Charset.forName("UTF-8")));

        return response.writeWith(Mono.just(buffer));
    }
}
