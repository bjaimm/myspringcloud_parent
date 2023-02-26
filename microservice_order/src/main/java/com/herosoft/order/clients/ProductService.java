package com.herosoft.order.clients;

import com.herosoft.commons.results.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "microservice-product")
public interface ProductService {
    @RequestMapping(method = RequestMethod.GET,value = "/products/{id}")
    Result findById(@PathVariable Integer id);

    @RequestMapping(method = RequestMethod.GET,value = "/products")
    Result findAll();
}
