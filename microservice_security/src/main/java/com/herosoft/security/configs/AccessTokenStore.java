package com.herosoft.security.configs;

import com.herosoft.commons.constants.TokenConstant;
import com.herosoft.security.dto.SecurityUserDetails;
import com.herosoft.security.service.SecurityUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 令牌配置
 */
@Configuration
@Slf4j
public class AccessTokenStore {

    @Autowired
    private SecurityUserDetailService securityUserDetailService;

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
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter(){
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                Map<String, Object> info = new LinkedHashMap(accessToken.getAdditionalInformation());

                if(authentication.getPrincipal() instanceof SecurityUserDetails) {
                    info.put("userid", ((SecurityUserDetails) authentication.getPrincipal()).getUserId());
                }
                else{
                    log.info("Oauth2的refresh token中产生的authentication对象，没有自定义的字段，需要重新获取放到刷新的token中");
                    SecurityUserDetails securityUserDetails = (SecurityUserDetails) securityUserDetailService.loadUserByUsername((String) authentication.getPrincipal());

                    info.put("userid",securityUserDetails.getUserId());
                }

                ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(info);

                return super.enhance(accessToken, authentication);
            }
        };

        converter.setSigningKey(TokenConstant.SIGNING_KEY);

        return converter;
    }
}
