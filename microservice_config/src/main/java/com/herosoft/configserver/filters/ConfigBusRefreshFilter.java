package com.herosoft.configserver.filters;

import com.herosoft.configserver.config.CustomHttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截git webhook的bus-refresh请求，清空body，解决400错误
 */
@Component
@Slf4j
public class ConfigBusRefreshFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String url = httpServletRequest.getRequestURI();

        if(!url.endsWith("/bus-refresh")){
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }

        log.info("截取到bus-refresh请求。。。");
        CustomHttpServletRequestWrapper wrapper = new CustomHttpServletRequestWrapper(httpServletRequest);

        filterChain.doFilter(wrapper,httpServletResponse);


    }
}
