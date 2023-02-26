package com.herosoft.security.service;

import com.herosoft.security.dto.LoginUserDto;

import java.util.Map;

public interface SecurityService {

    Map<String,String> login(LoginUserDto loginUser);
}
