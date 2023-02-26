package com.herosoft.movie.client;

import com.herosoft.movie.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService{
    @Override
    public UserDto findById(Integer id) {
        System.out.println("熔断发生。。。");
        return null;
    }
}
