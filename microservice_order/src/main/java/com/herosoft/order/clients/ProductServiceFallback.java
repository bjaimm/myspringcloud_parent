package com.herosoft.order.clients;

import com.herosoft.commons.results.Result;
import com.herosoft.order.dto.OrderDetailDto;
import com.herosoft.order.dto.UserDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
class ProductServiceFallback implements ProductService {
    private final Throwable cause;

    public ProductServiceFallback(Throwable cause){
        this.cause = cause;
    }

    @Override
    public Result findById(Integer id) {
        log.info("库存微服务无法访问，Hystrix短路器触发。。。异常原因:{}",cause.getLocalizedMessage());
        return Result.otherError(new Exception(cause));
    }

    @Override
    public Result findAll() {
        log.info("库存微服务无法访问，Hystrix短路器触发。。。异常原因:{}",cause.getLocalizedMessage());
        return Result.otherError(new Exception(cause));
    }

    @Override
    public Result deduceStock(List<OrderDetailDto> orderDetailDto) throws RuntimeException {
        log.info("库存微服务无法访问，Hystrix短路器触发。。。异常原因:{}",cause.getLocalizedMessage());
        throw new RuntimeException(cause);
    }
}
