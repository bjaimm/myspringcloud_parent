package com.herosoft.security.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
/**
 * 表单自定义认证成功处理器
 */
@Slf4j
public class SecurityAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        log.info("登录成功 SecurityAuthenticationSuccessHandler入口一");
        super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        setDefaultTargetUrl("/security/welcome");
        setAlwaysUseDefaultTargetUrl(true);

        log.info("IP:{}  用户:{}  于{}登录成功。。。",httpServletRequest.getRemoteAddr(),authentication.getName(), LocalDateTime.now());
        log.info("authentication : {}", SecurityContextHolder.getContext().getAuthentication());

        super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
    }
}
