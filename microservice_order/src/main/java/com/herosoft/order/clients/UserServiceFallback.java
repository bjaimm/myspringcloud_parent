package com.herosoft.order.clients;

import com.herosoft.commons.results.Result;
import com.herosoft.order.dto.UserDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class UserServiceFallback implements UserService {
    private final Throwable cause;

    public UserServiceFallback(Throwable cause){
        this.cause = cause;
    }

    @Override
    public Result updateBalance(UserDto userDto) {
        log.info("用户微服务无法访问，Hystrix短路器触发。。。异常原因:{}",cause.getLocalizedMessage());
        return Result.otherError(new Exception(cause));
    }

    @Override
    public Result findById(Integer id) {
        log.info("用户微服务无法访问，Hystrix短路器触发。。。异常原因:{}",cause.getLocalizedMessage());
        return Result.otherError(new Exception(cause));
    }
}
