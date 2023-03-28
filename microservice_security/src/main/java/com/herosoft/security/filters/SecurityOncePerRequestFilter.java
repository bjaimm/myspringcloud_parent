package com.herosoft.security.filters;

import com.alibaba.fastjson2.JSON;
import com.herosoft.commons.dto.JwtPayloadDto;
import com.herosoft.commons.utils.JwtUtils;
import com.herosoft.security.dto.SecurityUserDetails;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

@Component
@Slf4j
public class SecurityOncePerRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse reponse, FilterChain filterChain) throws ServletException, IOException {
        //从请求header中取出token，如果token不存在，直接放行，由Spring Security框架其他过滤器处理
        String token =request.getHeader("token");
        if(token == null){
            log.info("请求头中token字段没有获取到数据,将由后续security过滤器处理。。。");
            filterChain.doFilter(request, reponse);
            return;
        }
        log.info("从request中获取到token:{}",token);
        //解析token，获取到userId
        Integer userId;

        try {
            JwtPayloadDto payloadDto = jwtUtils.verifyTokenByHMAC(token,JwtUtils.JWT_HMAC_SECRET);
            userId =Optional.ofNullable(payloadDto).orElse(new JwtPayloadDto()).getUserId();
            log.info("解析后的token payload:{}",JSON.toJSONString(payloadDto));
        } catch (ParseException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        } catch (JOSEException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
        log.info("userId:{}",userId);

        //用userId在redis中查询出对应的SecurityUserDetails对象
        ValueOperations<String,String> redisPrincipal = redisTemplate.opsForValue();

        SecurityUserDetails userDetails = JSON.parseObject(redisPrincipal.get("userId:"+userId), SecurityUserDetails.class);

        log.info("Redis中获取的userDetails的value:{}",redisPrincipal.get("userId:"+userId));
        log.info("Redis中获取的userDetails的value Fastjson反序列后的对象:{}",JSON.toJSONString(userDetails));
        //log.info("Redis中获取的userDetails的value Gson反序列后的对象:{}", new Gson().toJson(new Gson().fromJson(redisPrincipal.get(),SecurityUserDetails.class)));

        //将查询到的SecurityUserDetail对象,组装Authentication，存入SecurityContextHolder
        if(userDetails!=null){
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request,reponse);
    }
}
