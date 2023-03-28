package com.herosoft.gateway.clients;

import com.herosoft.commons.results.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="microservice-security")
public interface SecurityService {
    @RequestMapping(method = RequestMethod.GET ,value = "/security/authority")
    Result findAuthorityAndRefreshRedis();

    @RequestMapping(method = RequestMethod.GET ,value = "/security/whitelist")
    Result findWhiteListAndRefreshRedis();

    @RequestMapping(method = RequestMethod.GET ,value = "/security/blacklist")
    Result findBlackListAndRefreshRedis();
}
