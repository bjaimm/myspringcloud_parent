package com.herosoft.user.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器是在servlet执行之前执行的程序（这里就是controller代码执行之前），它主要是用于拦截用户请求并作相应的处理，
 *比如说可以判断用户是否登录，做相关的日志记录，也可以做权限管理。SpringBoot中的拦截器实现和spring mvc 中是一样的，
 *它的大致流程是，先自己定义一个拦截器类，并将这个类实现一个 HandlerInterceptor 类，或者是继承 HandlerInterceptorAdapter ，
 *都可以实现拦截器的定义。然后将自己定义的拦截器注入到适配器中，也有两种方式，一种是实现 WebMvcConfigurer 接口，
 *一种是继承 WebMvcConfigurerAdapter 。下面我们来看看如何完成。
 *
 * @author HW
 * @date 2023/01/19
* */

public class MyInterceptor implements HandlerInterceptor {
    /**
     *
     * 自定义的拦截器可以实现HandlerInterceptor接口，也可以继承HandlerInterceptorAdapter类。
     *重写三个方法，当然也可以只实现一个最重要的preHandle方法。
     *preHandle方法：此方法会在进入controller之前执行，返回Boolean值决定是否执行后续操作。
     *postHandle方法：此方法将在controller执行之后执行，但是视图还没有解析，可向ModelAndView中添加数据(前后端不分离的)。
     *afterCompletion方法：该方法会在整个请求结束（请求结束，但是并未返回结果给客户端）之后执行， 可获取响应数据及异常信息。
     *
     * @param request
     * @param  response
     * @param handler
     *
     * @return boolean
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("进入MyInterceptor preHandle方法。。。");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("进入MyInterceptor postHandle方法。。。");
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("进入MyInterceptor afterCompletion方法。。。");
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
