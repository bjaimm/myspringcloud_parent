package com.herosoft.security.configs.system;

import com.alibaba.fastjson2.JSON;
import com.herosoft.commons.constants.SysConstant;
import com.herosoft.security.service.PermissionService;
import com.herosoft.security.service.RolePermissionService;
import com.herosoft.security.service.RoleService;
import com.herosoft.security.service.UrlListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 系统参数配置类
 *
 * 实现系统参数初始化到Redis，并提供同步的功能
 */
@Configuration
@Slf4j
public class SysParamsConfig {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private UrlListService urlListService;

    @Autowired
    private RoleService roleService;

    /**
     * 初始化系统参数到对应的Redis key
     */
    @PostConstruct
    public void init(){
        log.info("开始初始化Redis中的URL权限值。。。");
        HashOperations<String,String, List<String>> uriPathPermissions = redisTemplate.opsForHash();

        Map<String,List<String>> urlAuthorities;

        urlAuthorities=  rolePermissionService.findAllAuthorities();

        uriPathPermissions.putAll(SysConstant.SYS_URL_PATH_PERMISSION_MAPPINGS_REDIS_KEY,urlAuthorities);
        redisTemplate.expire(SysConstant.SYS_URL_PATH_PERMISSION_MAPPINGS_REDIS_KEY,3600,TimeUnit.SECONDS);

        log.info("开始初始化Redis中的白名单、黑名单列表。。。");
        ValueOperations<String,String> whiteList = redisTemplate.opsForValue();
        ValueOperations<String,String> blackList = redisTemplate.opsForValue();

        List<String> whiteUrls = new ArrayList<>();
        List<String> blackUrls = new ArrayList<>();

        urlListService.list().stream()
                .forEach((urlListPo)->{
                    if(urlListPo.getListType().equals(0)) {
                        whiteUrls.add(urlListPo.getUrl());
                    }
                    else if(urlListPo.getListType().equals(1)){
                        blackUrls.add(urlListPo.getUrl());
                    }
                });

        whiteList.set(SysConstant.SYS_WHITE_LIST_REDIS_KEY,JSON.toJSONString(whiteUrls),3600, TimeUnit.SECONDS);

        blackList.set(SysConstant.SYS_BLACK_LIST_REDIS_KEY,JSON.toJSONString(blackUrls),3600, TimeUnit.SECONDS);

    }

    public void refreshAuthoriteis(Map<String,List<String>> urlAuthorities){
        log.info("开始刷新Redis中的URL权限值。。。");
        HashOperations<String,String, List<String>> uriPathPermissions = redisTemplate.opsForHash();

        uriPathPermissions.putAll(SysConstant.SYS_URL_PATH_PERMISSION_MAPPINGS_REDIS_KEY,urlAuthorities);
        redisTemplate.expire(SysConstant.SYS_URL_PATH_PERMISSION_MAPPINGS_REDIS_KEY,3600,TimeUnit.SECONDS);

    }

    public void refreshWhiteList(List<String> whiteUrls){
        log.info("开始刷新Redis中的白名单。。。");
        ValueOperations<String,String> whiteList = redisTemplate.opsForValue();

        whiteList.set(SysConstant.SYS_WHITE_LIST_REDIS_KEY,JSON.toJSONString(whiteUrls),3600, TimeUnit.SECONDS);

    }

    public void refreshBlackList(List<String> blackUrls){
        log.info("开始刷新Redis中的黑名单。。。");
        ValueOperations<String,String> blackList = redisTemplate.opsForValue();

        blackList.set(SysConstant.SYS_BLACK_LIST_REDIS_KEY,JSON.toJSONString(blackUrls),3600, TimeUnit.SECONDS);

    }
}
