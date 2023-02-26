package com.herosoft.commons.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.herosoft.commons.annotations.NotControllerResponseAdvice;
import com.herosoft.commons.results.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ControllerResponseAdvise implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return !(methodParameter.getParameterType().isAssignableFrom(Result.class)||
                methodParameter.hasMethodAnnotation(NotControllerResponseAdvice.class));
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        //字符串类型不能直接包装
        if(methodParameter.getGenericParameterType().equals(String.class)){
            ObjectMapper mapper = new ObjectMapper();

            try {
                return mapper.writeValueAsString(Result.success(o));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return Result.success(o);
    }
}
