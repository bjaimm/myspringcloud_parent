package com.herosoft.gateway.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * 作为资源服务的配置类必须满足两个条件，如下：
 *
 * 标注注解@EnableResourceServer
 * 继承ResourceServerConfigurerAdapter
 *
 */
//@EnableResourceServer
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    /**
     * 配置资源Id和
     * 令牌校验服务(对于存在认证中心内存的令牌，需要配置进行远程校验，对于JWT令牌，可以不需要，直接在本地校验)
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("order")
        //对于存在认证中心内存的令牌，需要配置进行远程校验
        //.tokenServices(tokenServices())
        ;

    }

    /**
     * 配置Security的安全机制
     *
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //设置请求权限
        http.authorizeRequests()
                //all是在客户端中的scope
                .antMatchers("/**").access("#oauth2.hasScope('all')")
                .anyRequest().authenticated();
    }

}
