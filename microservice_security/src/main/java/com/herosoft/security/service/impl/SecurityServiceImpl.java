package com.herosoft.security.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.herosoft.commons.dto.JwtPayloadDto;
import com.herosoft.commons.utils.JwtUtils;
import com.herosoft.security.dto.LoginUserDto;
import com.herosoft.security.dto.SecurityUserDetails;
import com.herosoft.security.service.SecurityService;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;
    @Override
    public Map<String, String> login(LoginUserDto loginUser) {
        Map<String, String> result = new HashMap<>();
        String token;

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword());

        log.info("调用authenticationManager.authenticate方法。。。");

        Authentication authentication=authenticationManager.authenticate(authenticationToken);

        if(authentication == null){
            throw new RuntimeException("用户名密码错误！");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("authentication:{}", SecurityContextHolder.getContext().getAuthentication());

        //把Authentication中的principal存入redis
        log.info("userDetails:{}",JSON.toJSONString(authentication.getPrincipal()));
        SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();

        ValueOperations<String,String> redisPrincipal = redisTemplate.opsForValue();
        redisPrincipal.set("userId:"+userDetails.getUserId(), JSON.toJSONString(userDetails));

        //生成JWT Token，并返回
        try {
            Date now = new Date();
            Date exp = DateUtil.offsetSecond(now,60*60);

            JwtPayloadDto payloadDto = JwtPayloadDto.builder()
                    .sub("SecuriyLoginSuceess")
                    .jti(UUID.randomUUID().toString())
                    .iat(now.getTime())
                    .exp(exp.getTime())
                    .userId(userDetails.getUserId())
                    .username(userDetails.getUsername())
                    .authorities(userDetails.getAuthorities().stream()
                            .map(authority-> authority.getAuthority())
                            .collect(Collectors.toList()))
                    .build();

            log.info("token payload:{}",JSON.toJSONString(payloadDto));
            token = jwtUtils.generateJWTByHMAC(JSON.toJSONString(payloadDto),JwtUtils.JWT_HMAC_SECRET);
        } catch (JOSEException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
        result.put("access-token",token);

        return result;
    }
}
