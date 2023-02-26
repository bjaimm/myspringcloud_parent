package com.herosoft.security.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 表单自定义认证过滤器
 */
@Slf4j
public class SecurityUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("这里执行生成JWT Token的逻辑。。。");
        log.info("authResult:{}",authResult);

        super.successfulAuthentication(request, response, chain, authResult);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("SecurityUsernamePasswordAuthenticationFilter执行atemptAuthentication方法。。。");
        return super.attemptAuthentication(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("认证失败，SecurityUsernamePasswordAuthenticationFilter执行unsuccessfulAuthentication方法。。。Exception:{}",failed.getLocalizedMessage());
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
