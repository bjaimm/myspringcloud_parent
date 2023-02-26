package com.herosoft.order.clients;

import com.herosoft.commons.results.Result;
import com.herosoft.order.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "microservice-user")
public interface UserService {

    @RequestMapping(method = RequestMethod.PUT,value = "/users")
    Result updateBalance(@RequestBody UserDto userDto);

    @RequestMapping(method = RequestMethod.GET,value = "/users/{id}")
    Result findById(@PathVariable Integer id);
}
