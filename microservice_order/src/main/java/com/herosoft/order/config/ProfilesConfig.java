package com.herosoft.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * 以下设置profile的方式不可行
 */
@Configuration
public class ProfilesConfig extends SpringBootServletInitializer {

    @Autowired
    private ConfigurableEnvironment environment;

    ProfilesConfig() {
        //environment.setActiveProfiles("dev");
        System.out.println("通过ConfigurableEnvironment.setActiveProfiles设置dev。。。。");
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("设置spring.profiles.active=dev。。。。。。");
        //servletContext.setInitParameter("spring.profiles.active","dev");
    }
}
