package com.herosoft.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Configuration;

/**
 * 对于Feign调用，通过header传递Xid，实现分支事务启动
 */
@Configuration
@Slf4j
public class FeignConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {

        //实际测试下来，发现这里取到的xid是null
        String xid = RootContext.getXID();

        log.info("拦截调用库存服务，放入全局事务XID:{}",xid);
        if(!StringUtils.isEmpty(xid)){

            requestTemplate.header(RootContext.KEY_XID,xid);
        }
    }
}
