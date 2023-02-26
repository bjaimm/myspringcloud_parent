package com.herosoft.commons.handlers;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("MybatisPlus Insert开始自动填充。。。");
        //strictXXX为严格填充，属性存在值不覆盖，填充值为null不填充
        this.strictInsertFill(metaObject,"createdt", LocalDateTime.class,LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedt", LocalDateTime.class,LocalDateTime.now());
        this.strictInsertFill(metaObject,"createTime", LocalDateTime.class,LocalDateTime.now());
        this.strictInsertFill(metaObject,"updateTime", LocalDateTime.class,LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("MybatisPlus Update开始自动填充。。。");
        //this.strictUpdateFill(metaObject,"update_dt", LocalDateTime.class,LocalDateTime.now());
        this.setFieldValByName("updatedt",LocalDateTime.now(),metaObject);
        this.setFieldValByName("updateTime",LocalDateTime.now(),metaObject);
    }
}
