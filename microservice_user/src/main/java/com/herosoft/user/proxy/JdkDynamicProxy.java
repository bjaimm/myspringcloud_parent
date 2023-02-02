package com.herosoft.user.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class JdkDynamicProxy implements InvocationHandler {
    private Object bean;
    private final static String METHOD_WAKEUP = "wakeup";
    private final static String METHOD_SLEEP="sleep";

    private final static String METHOD_EAT="eat";

    public JdkDynamicProxy(){};

    public JdkDynamicProxy(Object bean){
        this.bean = bean;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();

        if(METHOD_WAKEUP.equals(methodName)){
            log.info("动态代理：早安啊，{}。。。",bean.toString());
        }else if (METHOD_SLEEP.equals(methodName)) {
            log.info("动态代理：晚安啊，{}。。。",bean.toString());
        }else if (METHOD_EAT.equals(methodName)) {
            log.info("动态代理：吃{}吧,{}。。。",args.toString(),bean.toString());
        }

        return method.invoke(bean, args);
    }
}
