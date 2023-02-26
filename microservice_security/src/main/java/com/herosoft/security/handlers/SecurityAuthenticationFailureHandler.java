package com.herosoft.security.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 表单自定义认证失败处理器
 */
@Slf4j
public class SecurityAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        setDefaultFailureUrl("/security/failLogin");
        log.info("认证失败调用SecurityAuthenticationFailureHandler -> onAuthenticationFailure Exception:{}",e.getLocalizedMessage());

        super.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
    }
}
