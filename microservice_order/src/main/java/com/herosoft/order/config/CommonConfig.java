package com.herosoft.order.config;

import com.herosoft.commons.handlers.MyBatisPlusMetaObjectHandler;
import org.springframework.context.annotation.Bean;

/**
 * 已在commons包中的spring.factories自动加载，这里保留这种方式的例子
 *
 * @author ZZ02BG672
 */
//@Configuration
public class CommonConfig {

    /**
     * MyBatisPlusMetaObjectHandler实现自动注入实体类指定字段的逻辑
     *
     * @return
     */
    @Bean
    MyBatisPlusMetaObjectHandler myBatisPlusMetaObjectHandler(){
        return new MyBatisPlusMetaObjectHandler();
    }
}
