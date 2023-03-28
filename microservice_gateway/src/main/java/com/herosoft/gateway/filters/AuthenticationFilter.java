package com.herosoft.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@Slf4j
public class AuthenticationFilter implements Ordered {

    @Bean
    public GlobalFilter globalFilter() {
        //GlobalFilter是只包含有一个方法的接口，即函数式接口，可通过下面匿名内部类的方式进行接口方法实现，
        //并将实例注入容器。
        //GlobalFilter接口的实现类会处理每一个请求
        return ((exchange, chain) -> {
            log.info("GlobalFilter开始执行。。。。");
            return chain.filter(exchange);
        });
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
