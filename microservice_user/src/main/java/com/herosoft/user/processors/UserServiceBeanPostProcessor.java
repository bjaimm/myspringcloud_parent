package com.herosoft.user.processors;

import com.herosoft.user.service.impl.UserServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class UserServiceBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof UserServiceImpl){
            System.out.println("UserServiceBeanPostProcessor is called...");
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
