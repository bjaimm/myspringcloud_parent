package com.herosoft.gateway;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public KeyResolver ipKeyResolver(){
		return new KeyResolver() {
			@Override
			public Mono<String> resolve(ServerWebExchange exchange) {
				return Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
			}
		};
	}

	/**
	 * 在Spring cloud gateway下，Feign调用会失败，需要手动注入下面的bean
	 * @param converters
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public HttpMessageConverters converters(ObjectProvider<HttpMessageConverter<?>> converters){
		return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
	}
}
