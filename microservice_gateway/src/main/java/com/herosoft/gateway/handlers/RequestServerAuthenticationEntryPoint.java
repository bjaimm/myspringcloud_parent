package com.herosoft.gateway.handlers;

import com.alibaba.fastjson2.JSON;
import com.herosoft.commons.results.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * 如果令牌失效或者过期，则会直接返回，这里需要定制提示信息。
 */
@Component
@Slf4j
public class RequestServerAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange serverWebExchange, AuthenticationException ex) {
        log.info("开始定制认证失败后的返回信息。。。");
        ServerHttpResponse response = serverWebExchange.getResponse();

        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String responseBody = JSON.toJSONString(Result.otherError(ex));

        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(Charset.forName("utf-8")));
        return response.writeWith(Mono.just(buffer));
    }
}
