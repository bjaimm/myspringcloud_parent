package com.herosoft.security.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.herosoft.commons.annotations.NotControllerResponseAdvice;
import com.herosoft.commons.results.Result;
import com.herosoft.security.configs.system.SysParamsConfig;
import com.herosoft.security.dto.GetTokenDto;
import com.herosoft.security.dto.LoginUserDto;
import com.herosoft.security.po.UrlListPo;
import com.herosoft.security.service.RolePermissionService;
import com.herosoft.security.service.SecurityService;
import com.herosoft.security.service.UrlListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/security")
@Slf4j
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SysParamsConfig sysParamsConfig;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private UrlListService urlListService;

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @RequestMapping(value = "/status",produces = MediaType.APPLICATION_JSON_VALUE)
    public String checkStatus(){
        return "鉴权服务正常工作。。。";
    }

    @RequestMapping("/failLogin")
    public String failLogin(){
        return "登录失败。。。";
    }

    @PreAuthorize("hasRole('Admin')")
    @RequestMapping("/welcome")
    @NotControllerResponseAdvice
    public String welcome(){
        log.info("welcome authentication : {}", SecurityContextHolder.getContext().getAuthentication());
        return "This welcome page, congratulation, you have successfully accessed inside.";
    }

    @RequestMapping(value="/login",method = RequestMethod.POST)
    public Result login(@RequestBody LoginUserDto loginUser){

        Map<String,String> result = securityService.login(loginUser);

        log.info("login authentication : {}", SecurityContextHolder.getContext().getAuthentication());
        return Result.success(result);
    }

    @RequestMapping("/user")
    public String userRootPath(){
        return "User角色用户的根目录";
    }

    @RequestMapping("/p1")
    public String p1(){
        return "权限securiy_p1可以访问这里";
    }

    @PreAuthorize("hasAuthority('security_p2')")
    @RequestMapping("/p2")
    public String p2(){
        return "权限securiy_p2可以访问这里";
    }

    @RequestMapping(value = "/authority",method = RequestMethod.GET)
    public Map<String,List<String>> findAuthorityAndRefreshRedis(){
        log.info("收到读取URL权限列表，并刷新Redis键值请求。。。");
        Map<String,List<String>> urlAuthorities = rolePermissionService.findAllAuthorities();

        sysParamsConfig.refreshAuthoriteis(urlAuthorities);

        return urlAuthorities;
    }

    @RequestMapping(value = "/whitelist",method = RequestMethod.GET)
    public List<String> findWhiteListAndRefreshRedis(){
        log.info("收到读取白名单列表，并刷新Redis键值请求。。。");
        List<String> whiteList = urlListService.list(Wrappers.lambdaQuery(UrlListPo.class)
                .eq(UrlListPo::getListType,0))
                .stream()
                .map(urlListPo -> urlListPo.getUrl())
                .collect(Collectors.toList());
        log.info("白名单列表:{}",whiteList);
        sysParamsConfig.refreshWhiteList(whiteList);

        return whiteList;
    }

    @RequestMapping(value = "/blacklist",method = RequestMethod.GET)
    public List<String> findBlackListAndRefreshRedis(){
        log.info("收到读取黑名单列表，并刷新Redis键值请求。。。");
        List<String> blackList = urlListService.list(Wrappers.lambdaQuery(UrlListPo.class)
                        .eq(UrlListPo::getListType,1))
                .stream()
                .map(urlListPo -> urlListPo.getUrl())
                .collect(Collectors.toList());

        sysParamsConfig.refreshWhiteList(blackList);

        return blackList;
    }

    @RequestMapping(value = "/getToken",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public OAuth2AccessToken getToken(@RequestBody GetTokenDto getTokenDto) throws HttpRequestMethodNotSupportedException {
        log.info("收到获取token的request, 请求头包含信息:{}",getTokenDto.toString());

        Map<String, String> parameters = new HashMap<>();

        parameters.put("username",getTokenDto.getUserName());
        parameters.put("password",getTokenDto.getPassword());
        parameters.put("client_id",getTokenDto.getClientId());
        parameters.put("client_secret",getTokenDto.getClientSecret());
        parameters.put("grant_type",getTokenDto.getGrantType());

        return tokenEndpoint.postAccessToken(
                new UsernamePasswordAuthenticationToken(getTokenDto.getUserName(),getTokenDto.getPassword()),
                parameters).getBody();

    }
}
