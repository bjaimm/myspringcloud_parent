package com.herosoft.gateway.configs;

import com.herosoft.commons.constants.TokenConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * 令牌配置
 */
@Configuration
public class AccessTokenStore {
    @Bean
    public TokenStore tokenStore(){
        //使用JWT TokenStore生成令牌
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * JwtAccessTokenConverter是TokenEnhancer子类，用于JWT令牌和Oauth2身份验证信息之间转换
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        converter.setSigningKey(TokenConstant.SIGNING_KEY);

        return converter;
    }
}
