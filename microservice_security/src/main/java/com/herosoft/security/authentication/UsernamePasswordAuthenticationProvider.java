package com.herosoft.security.authentication;

import com.herosoft.security.service.impl.SecurityUserDetailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    SecurityUserDetailServiceImpl userDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userId = authentication.getName();
        String password = authentication.getCredentials().toString();
        log.info("UsernamePasswordAuthenticationProvider开始执行authenticate方法。。。");

        UserDetails userDetails = userDetailService.loadUserByUsername(userId);
        log.info("rawpassword:{} -> encodedpassword:{}",password,passwordEncoder.encode(password));
        boolean passwordValidated = passwordEncoder.matches(password, passwordEncoder.encode(userDetails.getPassword()));

        if (!passwordValidated) {
            throw new AuthenticationException("用户名密码不匹配"){};
        }
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
