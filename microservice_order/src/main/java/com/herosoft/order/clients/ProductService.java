package com.herosoft.order.clients;

import com.herosoft.commons.results.Result;
import com.herosoft.order.dto.OrderDetailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "microservice-product",fallbackFactory = ProductServiceFallbackFactory.class)
public interface ProductService {
    @RequestMapping(method = RequestMethod.GET,value = "/products/{id}")
    Result findById(@PathVariable Integer id);

    @RequestMapping(method = RequestMethod.GET,value = "/products")
    Result findAll();
    @RequestMapping(method = RequestMethod.PUT,value="/products/deduceStock",produces = "application/json")
    Result deduceStock(@RequestBody List<OrderDetailDto> orderDetailDto) throws RuntimeException;
}
