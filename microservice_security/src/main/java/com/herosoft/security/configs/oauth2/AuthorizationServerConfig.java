package com.herosoft.security.configs.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * OAuth2.0认证中心的配置类,必须满足以下两点：
 *
 * 1.@EnableAuthorizationServer注解
 * 2.集成AuthorizationServerConfigurerAdpater
 *
 */
@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 令牌访问端点配置
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authorizationCodeServices(authorizationCodeServices())
                .authenticationManager(authenticationManager)
                .tokenServices(tokenServices())
                .allowedTokenEndpointRequestMethods(HttpMethod.POST,HttpMethod.GET)
                .pathMapping("/oauth/token","/security/oauth/token")
                .pathMapping("/oauth/check_token","/security/oauth/check_token")
                .pathMapping("/oauth/authorize","/security/oauth/authorize")
                .pathMapping("/oauth/error","/security/oauth/error")
                .pathMapping("/oauth/token_key","/security/oauth/token_key")
                .pathMapping("/oauth/confirm_access","/security/oauth/confirm_access");

    }

    /**
     * 令牌端点安全约束配置
     *
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security//开启/oauth/token_key验证端口访问权限
                .tokenKeyAccess("permistAll()")
                //开启 /oauth/check_token验证端口认证权限访问
                .checkTokenAccess("permitAll()")
                //表示支持client id 和client secret做登录认证
                .allowFormAuthenticationForClients();

    }

    /**
     * 客户端详情配置，比如client id, client secret
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                //客户端Id
                .withClient("clientId")
                //客户端密钥
                .secret(passwordEncoder.encode("123"))
                //资源Id，唯一，可以设置多个
                .resourceIds("users","orders")
                //授权模式
                .authorizedGrantTypes("authorization_code","password","client_credentials","implicit","refresh_token")
                //允许的授权范围，这里的all只是一种标识，可以自定义
                .scopes("all")
                //false 跳转到授权页面 true 直接返回授权码
                .autoApprove(true)
                //授权码模式的回调地址
                .redirectUris("http://www.baidu.com");
    }

    /**
     * 授权码模式必须注入的服务，用来颁布和删除授权码，也支持数据库存储
     * @return
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {

        return new InMemoryAuthorizationCodeServices();
    }

    /**
     * 令牌管理服务的配置，用来创建、获取、刷新令牌，
     */
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices services = new DefaultTokenServices();

        //客户端配置策略
        //services.setClientDetailsService(inMemoryClientDetailsService());

        //支持令牌刷新
        services.setSupportRefreshToken(true);

        //设置令牌服务
        services.setTokenStore(tokenStore);

        //设置访问令牌过期时间
        services.setAccessTokenValiditySeconds(60*60);

        //设置刷新令牌过期时间
        services.setRefreshTokenValiditySeconds(60*60*24*2);

        //设置令牌增强
        services.setTokenEnhancer(jwtAccessTokenConverter);

        return services;
    }
    @Bean
    public InMemoryClientDetailsService inMemoryClientDetailsService(){
        return new InMemoryClientDetailsService();
    }

}
