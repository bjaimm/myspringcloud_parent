package com.herosoft.security.handlers;

import com.alibaba.fastjson.JSON;
import com.herosoft.commons.enums.ResponseEnum;
import com.herosoft.commons.exceptions.DefinitionException;
import com.herosoft.commons.results.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 自定义认证异常处理
 */
@Slf4j
@Component
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        log.info("认证异常：{}",e.getLocalizedMessage());

        String jsonException = JSON.toJSONString(new Result<>(false,
                ResponseEnum.NO_AUTHENTICATION.getReponseCode(),
                ResponseEnum.NO_AUTHENTICATION.getReponseMessage(),
                e.getLocalizedMessage()));

        httpServletResponse.setStatus(200);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");

        httpServletResponse.getWriter().write(jsonException);
    }
}
