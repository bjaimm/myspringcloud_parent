package com.herosoft.commons.aops;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class TakeLogAspect {

    private  static ThreadLocal<Long> startTime = new ThreadLocal<>();
    private static ThreadLocal<Long> endTime = new ThreadLocal<>();

    /*
    @annotation匹配注解
    * */
    @Pointcut("@annotation(com.herosoft.commons.annotations.TakeLog)")
    /*
    execution方式匹配切点
    第一个*匹配返回值为所有类型
    第二个*匹配所有类
    第三个*匹配所有方法
    (..)匹配所有参数
    * */
    //@Pointcut("execution(* com.herosoft.*.controller.*.*(..))")
    public void takeLogPointCut()
    {

    }

    @Before("takeLogPointCut()")
    public void doBefore(JoinPoint joinPoint) throws Throwable{
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        StringBuilder stringBuilder = new StringBuilder();
        for(Object object:args){
            stringBuilder.append(object+";");
        }

        log.info("进入方法《{}》，参数为{}", methodName, stringBuilder);

        startTime.set(System.currentTimeMillis());

        //接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        log.info("请求URL:"+request.getRequestURL().toString());
        log.info("请求Method:"+request.getMethod());
    }

    /*
    returning值需和doAfterReturning方法的参数名称一致
    * */
    @AfterReturning(pointcut = "takeLogPointCut()",returning = "ret")
    public void doAfterReturning(Object ret){
        log.info("@AfterReturning 执行。。。");
        log.info("方法返回值："+ JSON.toJSONString(ret));
        endTime.set(System.currentTimeMillis());
        log.info("方法执行用时："+(endTime.get()-startTime.get())+"豪秒");

    }

    @After(value = "takeLogPointCut()")
    public void doAfter(){
        log.info("@After 执行。。。");
        endTime.set(System.currentTimeMillis());
        log.info("方法执行用时："+(endTime.get()-startTime.get())+"豪秒");
    }

    @AfterThrowing(pointcut = "takeLogPointCut()",throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint,Throwable e){
        log.info("@AfterThrowing 执行。。。");
        log.info("方法异常："+e.getLocalizedMessage());
    }

    public void remove(){
        startTime.remove();
        endTime.remove();
    }

}
