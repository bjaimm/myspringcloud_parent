package com.herosoft.gateway.configs;

import com.alibaba.fastjson2.JSON;
import com.herosoft.commons.constants.SysConstant;
import com.herosoft.gateway.clients.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

/**
 * 新建SecurityConfig这个配置类，标注注解 @EnableWebFluxSecurity，
 * 注意不是 @EnableWebSecurity，因为Spring Cloud Gateway是基于Flux实现的
 *
 * 需要通过SecurityWebFilterChain及ServerHttpSecurity关闭csrf
 * 否则路由下游服务会产生阻止信息，CSRF Token has been associated to this client
 */
@EnableWebFluxSecurity
@Configuration
@Slf4j
public class SecurityConfig {

    @Autowired
    private ReactiveAuthenticationManager jwtAuthenticationManager;

    @Autowired
    private ReactiveAuthorizationManager jwtAuthorizationManager;

    @Autowired
    private ServerAccessDeniedHandler requestServerAccessDeniedHandler;

    @Autowired
    private ServerAuthenticationEntryPoint requestServerAuthenticationEntryPoint;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SecurityService securityService;

    @Bean
    public CorsWebFilter corsFilter(){
        return new CorsWebFilter(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
                return null;
            }
        });
    }
    @Bean
    SecurityWebFilterChain webFilterChain(ServerHttpSecurity http){
        //创建认证过滤器
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());

        List<String> whiteList = new ArrayList<>();
        String[] pathMatcher;

        ValueOperations opsForValue= redisTemplate.opsForValue();
        Object object = opsForValue.get(SysConstant.SYS_WHITE_LIST_REDIS_KEY);
        if(object != null){
            whiteList.addAll(JSON.parseObject(object.toString(), ArrayList.class));
            log.info("从Redis中获取白名单。。。");
        }
        else
        {
            Object data = securityService.findWhiteListAndRefreshRedis().getData();
            String text = JSON.toJSONString(data);
            whiteList.addAll(JSON.parseObject(text, ArrayList.class));
            log.info("从DB获取白名单。。。");
        }

        pathMatcher=whiteList.toArray(new String[whiteList.size()]);

        //关闭跨域请求伪造检查
        http.csrf().disable();

        //关闭Basic Http认证
        http.httpBasic().disable();

        //设置鉴权管理器
        http.authorizeExchange()
                .pathMatchers( pathMatcher).permitAll()
                .anyExchange()
                .access(jwtAuthorizationManager);

        //设置异常处理器
        http.exceptionHandling()
                .authenticationEntryPoint(requestServerAuthenticationEntryPoint)
                .accessDeniedHandler(requestServerAccessDeniedHandler);

        //设置跨域过滤器
        http.addFilterAt(corsFilter(), SecurityWebFiltersOrder.CORS);

        //设置认证过滤器
        http.addFilterAt(authenticationWebFilter,SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();

    }
}
