package com.herosoft.order.clients;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceFallbackFactory implements FallbackFactory<ProductService> {
    @Override
    public ProductService create(Throwable cause) {
        return new ProductServiceFallback(cause);
    }
}
