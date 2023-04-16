package com.herosoft.product.filters;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.herosoft.commons.constants.RequestConstant;
import com.herosoft.commons.constants.TokenConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LoginAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(TokenConstant.AUTHORIZED_TOKEN_NAME);

        if(!StringUtils.isEmpty(token)){
            log.info("收到携带Token认证用户的请求。。。");
            JSONObject jsonObject = JSON.parseObject(Base64.decodeStr(token));

            Integer userId = (Integer) jsonObject.get("userId");
            String userName = (String) jsonObject.get("userName");
            List<String> authorities = (List<String>) jsonObject.get("authorities");

            log.info("用户ID:{}", userId);
            log.info("用户名称:{}", userName);
            log.info("用户权限:{}", authorities);

            Map<String,Object> loginAttributes = new HashMap<>();

            loginAttributes.put("userId",userId );
            loginAttributes.put("userName",userName);
            loginAttributes.put("authorities",authorities);

            request.setAttribute(RequestConstant.LOGIN_ATTRIBUTES,loginAttributes);
        }
        filterChain.doFilter(request,response);
    }
}
