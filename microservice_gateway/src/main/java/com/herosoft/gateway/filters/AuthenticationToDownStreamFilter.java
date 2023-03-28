package com.herosoft.gateway.filters;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.herosoft.commons.constants.SysConstant;
import com.herosoft.commons.constants.TokenConstant;
import com.herosoft.gateway.clients.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AuthenticationToDownStreamFilter implements GlobalFilter, Ordered {

    @Autowired
    private TokenStore tokenStore;

   // @Autowired
    //private RedissonClient redissonClient;

    //@Autowired
    //private RedissonReactiveClient redissonReactiveClient;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String headerAutorizaiton ;
        String token;
        OAuth2Authentication authentication;
        OAuth2AccessToken accessToken;
        ServerHttpRequest newRequest;
        ServerWebExchange newExchange;

        log.info("开始在请求头中封装认证信息对象，传递给下游。。。");

        ServerHttpResponse response = exchange.getResponse();
        List<String> whiteList = new ArrayList<>();

        //这里调用RedissonClient会产生异常：sync methods can't be invoked from async/rx/reactive listeners
        /*RBucket<String> whiteListRedis= redissonClient.getBucket(SysConstant.SYS_WHITE_LIST_REDIS_KEY);

        if (whiteListRedis.isExists()){
            String jsonString = JSON.toJSONString(whiteListRedis.get());
            whiteList.addAll(JSON.parseArray(jsonString,String.class));
            log.info("从Redis中获取白名单。。。");
        }
        else{

            Object data = securityService.findWhiteListAndRefreshRedis().getData();
            String text = JSON.toJSONString(data);
            whiteList.addAll(JSON.parseObject(text, ArrayList.class));
            log.info("从DB获取白名单。。。");
        }*/
        //用redissonReactiveClient不会报错，但是返回结果要用Mono类异步处理
        /*RBucketReactive<String> whiteListRedis= redissonClient.reactive().getBucket(SysConstant.SYS_WHITE_LIST_REDIS_KEY);

        whiteListRedis.isExists().subscribe(exist ->{
            if(exist){
                whiteListRedis.get().subscribe(keyvalue ->{
                    String temp = JSON.toJSONString(keyvalue);
                    whiteList.addAll(JSON.parseArray(temp, String.class));
                    log.info("从Redis中获取白名单。。。");
                });
            }
            else{
                Object data = securityService.findWhiteListAndRefreshRedis().getData();
                String text = JSON.toJSONString(data);
                whiteList.addAll(JSON.parseObject(text, ArrayList.class));
                log.info("从DB获取白名单。。。");
            }
        });*/

        /*改用Redistemplate的话，如果同时引入redisson,需要spring boot 2.3以下版本
        否则会产生java.lang.NoClassDefFoundError: org/springframework/data/redis/connection/zset/Tuple
        */
        ValueOperations opsForValue= redisTemplate.opsForValue();
        Object object = opsForValue.get(SysConstant.SYS_WHITE_LIST_REDIS_KEY);
        if(object != null){
            whiteList.addAll(JSON.parseObject(object.toString(),ArrayList.class));
            log.info("从Redis中获取白名单。。。");
        }
        else
        {
            Object data = securityService.findWhiteListAndRefreshRedis().getData();
            String text = JSON.toJSONString(data);
            whiteList.addAll(JSON.parseObject(text, ArrayList.class));
            log.info("从DB获取白名单。。。");
        }

        log.info("白名单:{}",whiteList);
        String requestUrl = exchange.getRequest().getURI().getPath();

        if(whiteList.contains(requestUrl)){
            log.info("白名单放行，不封装token，发送给下游服务。。。");
            return chain.filter(exchange);
        }

        headerAutorizaiton= exchange.getRequest().getHeaders().getFirst(TokenConstant.HEADER_AUTHORIZATION);
        token = headerAutorizaiton.substring(TokenConstant.BEARER_TOKEN_PREFIX_LENGTH);

        authentication = this.tokenStore.readAuthentication(token);
        accessToken = this.tokenStore.readAccessToken(token);

        if(authentication==null){
            return Mono.error(new InvalidTokenException("没有携带被认证的Token!"));
        }
        try {
            String userName = accessToken.getAdditionalInformation().get("user_name").toString();
            Integer userId = Integer.parseInt(accessToken.getAdditionalInformation().get("userid").toString());

            List<String> authorities = (List<String>) accessToken.getAdditionalInformation().get("authorities");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(TokenConstant.AUTHORIZED_USER_NAME,userName);
            jsonObject.put(TokenConstant.AUTHORIZED_USER_ID,userId);
            jsonObject.put(TokenConstant.AUTHORIZED_AUTHORITIES,authorities);

            String base64 = Base64.encode(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
            log.info("userId:{}", userId);
            log.info("userName:{}", userName);
            //把权限信息封装在请求头
            newRequest = exchange.getRequest()
                    .mutate()
                    .header(TokenConstant.AUTHORIZED_TOKEN_NAME,base64)
                    .build();

            newExchange=exchange.mutate()
                    .request(newRequest)
                    .build();

        }
        catch (InvalidTokenException e){
            return Mono.error(new InvalidTokenException(e.getLocalizedMessage()));
        }

        return chain.filter(newExchange);

    }

    @Override
    public int getOrder() {
        return 3;
    }
}
