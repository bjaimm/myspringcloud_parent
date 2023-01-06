package com.herosoft.movie.client;

import com.herosoft.movie.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "microservice-user",fallback = UserServiceImpl.class)
@Component
public interface UserService {

    @RequestMapping(value = "/users/{id}")
    public User findById(@PathVariable(value = "id") Integer id);
}
