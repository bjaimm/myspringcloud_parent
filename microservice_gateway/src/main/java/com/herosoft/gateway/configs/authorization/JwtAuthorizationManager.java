package com.herosoft.gateway.configs.authorization;

import cn.hutool.core.text.AntPathMatcher;
import com.alibaba.fastjson2.JSON;
import com.herosoft.commons.constants.SysConstant;
import com.herosoft.gateway.clients.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 鉴权管理器
 */
@Component
@Slf4j
public class JwtAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    //@Autowired
    //private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SecurityService securityService;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        log.info("鉴权管理器开始校验请求中token中的权限。。。{}",authorizationContext.getExchange().getRequest().getMethod());

        URI uri;
        String requestMethod;
        List<String> authorities ;
        Map<String,List<String>> urlAuthorities;
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        //RMap<String, List<String>> uriPathPermissions ;
        HashOperations<String,String,List<String>> hashOperations;

        uri = authorizationContext.getExchange().getRequest().getURI();

        requestMethod= authorizationContext.getExchange().getRequest().getMethod().toString();

        //uriPathPermissions= redissonClient.getMap(SysConstant.SYS_URL_PATH_PERMISSION_MAPPINGS_REDIS_KEY);
        hashOperations  = redisTemplate.opsForHash();
        urlAuthorities=hashOperations.entries(SysConstant.SYS_URL_PATH_PERMISSION_MAPPINGS_REDIS_KEY);
        //if(uriPathPermissions.isEmpty()){
        if (urlAuthorities.isEmpty()){
            urlAuthorities = JSON.parseObject(JSON.toJSONString(securityService.findAuthorityAndRefreshRedis().getData()), Map.class);
            log.info("从DB获取访问当前URL的权限列表。。。");
        }
        else {
            //urlAuthorities=uriPathPermissions.readAllMap();
            //urlAuthorities=hashOperations.entries(SysConstant.SYS_URL_PATH_PERMISSION_MAPPINGS_REDIS_KEY);
            log.info("从Redis获取到的访问当前URL的权限列表。。。");
        }
        authorities=urlAuthorities.entrySet().stream()
                .filter(entry -> antPathMatcher.match(entry.getKey()+"/**",requestMethod+":"+uri.getPath()))
                .flatMap(item-> item.getValue().stream())
                .collect(Collectors.toList());

        log.info("访问当前路径需要的权限列表:{}", authorities);

        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(authetication ->{
                    log.info("用户的权限列表:{}",authetication.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authetication);
                    return authetication.getAuthorities();
                })
                .map(GrantedAuthority::getAuthority)
                //系统权限配置和用户权限有交集，则返回true，无则false
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
