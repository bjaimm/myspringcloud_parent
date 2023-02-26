package com.herosoft.gateway.configs.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 认证管理器自定义
 *
 * 需要实现ReactiveAuthenticationManager这个接口。
 * 认证管理的作用就是获取传递过来的令牌，对其进行解析、验签、过期时间判定。
 */
@Component
@Slf4j
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    @Autowired
    private TokenStore tokenStore;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("认证管理器开始解析请求携带的令牌，并验签，过期时间判断。。。");
        log.info("传递的Authentication信息：{}",authentication);

        return Mono.justOrEmpty(authentication)
                .filter(a -> a instanceof BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class)
                .map(BearerTokenAuthenticationToken::getToken)
                .flatMap(token ->{
                    OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(token);

                    if (accessToken == null) {
                        return Mono.error(new InvalidTokenException("无效的Token!"));
                    }
                    else if(accessToken.isExpired()){
                        return Mono.error(new InvalidTokenException("Token已过期!"));
                    }

                    OAuth2Authentication oAuth2Authentication = this.tokenStore.readAuthentication(token);
                    if(oAuth2Authentication==null){
                        return Mono.error(new InvalidTokenException("没有被认证的Token!"));
                    }else{
                        return Mono.just(oAuth2Authentication);
                    }
                })
                .cast(Authentication.class);
    }
}
