package com.herosoft.user.handler;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

public class HandlerContext {
    private Map<String, Class> handlerMap;

    public HandlerContext(Map<String,Class> handlerMap) {
        System.out.println("HandlerContext->All args contruction method is called...");
        this.handlerMap=handlerMap;
    }
    public HandlerContext(){
        System.out.println("HandlerContext->No args contruction method is called...");
    }

    public AbstractHandler getInstance(String type){
        Class clazz = handlerMap.get(type);
        if(clazz == null){
            throw new IllegalArgumentException("not found handler for type:"+type);
        }

        return (AbstractHandler) BeanTool.getBean(clazz);
    }


}
