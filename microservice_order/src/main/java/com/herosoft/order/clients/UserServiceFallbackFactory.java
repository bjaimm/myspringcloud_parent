package com.herosoft.order.clients;

//import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import feign.hystrix.FallbackFactory;

@Component
class UserServiceFallbackFactory implements FallbackFactory<UserService> {

    @Override
    public UserService create(Throwable cause) {
        return new UserServiceFallback(cause);
    }
}
