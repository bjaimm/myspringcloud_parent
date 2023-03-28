package com.herosoft.gateway.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.herosoft.commons.enums.ResponseEnum;
import com.herosoft.commons.results.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GloablErrorExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        log.info("开始处理发现的全局异常。。。异常描述：{}",throwable.getLocalizedMessage());
        log.info("请求携带Token中的Authentication:{}", SecurityContextHolder.getContext().getAuthentication());

        ServerHttpResponse response = exchange.getResponse();
        //检查服务端是否已将结果返回客户端，如果已提交则无需后续处理
        if(response.isCommitted()){
            return Mono.error(throwable);
        }

        Result result = Result.responseEnum(ResponseEnum.NO_AUTHENTICATION,throwable.getLocalizedMessage());

        //JSON格式响应
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if(throwable instanceof ResponseStatusException){
            response.setStatusCode(((ResponseStatusException) throwable).getStatus());
        }
        if(throwable instanceof InvalidTokenException){
            result = Result.otherError(new Exception(throwable.getLocalizedMessage()));
        }
        Result finalResult = result;

        return response.writeWith(Mono.fromSupplier(()->{
            DataBufferFactory factory = response.bufferFactory();
            try {
                //根据业务需要，可以定义返回内容
                return factory.wrap(new ObjectMapper().writeValueAsBytes(finalResult));
            } catch (JsonProcessingException e) {
                log.info("返回异常响应处理结果出错，原因：{}",e.getLocalizedMessage());
                return factory.wrap(new byte[0]);
            }
        }));
    }
}
