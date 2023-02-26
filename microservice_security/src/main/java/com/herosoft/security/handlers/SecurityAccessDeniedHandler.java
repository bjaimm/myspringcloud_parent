package com.herosoft.security.handlers;

import com.alibaba.fastjson.JSON;
import com.herosoft.commons.enums.ResponseEnum;
import com.herosoft.commons.results.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        log.info("授权异常：{}",e.getLocalizedMessage());

        String jsonException = JSON.toJSONString(new Result<>(false,
                ResponseEnum.NO_PERMISSION.getReponseCode(),
                ResponseEnum.NO_PERMISSION.getReponseMessage(),
                e.getLocalizedMessage()));

        httpServletResponse.setStatus(200);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");

        httpServletResponse.getWriter().write(jsonException);
    }
}
