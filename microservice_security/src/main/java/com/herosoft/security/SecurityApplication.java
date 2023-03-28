package com.herosoft.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class SecurityApplication {

	private static ConfigurableEnvironment environment;
	@Autowired
	public void setEnvironment(ConfigurableEnvironment environment) {
		SecurityApplication.environment = environment;
	}

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
		//System.getProperty是获取Java的系统属性，如JVM命令行参数 -D
		System.out.println("System -> key.value:"+System.getProperty("key.value"));
		//environment.getProperty优先程序命令行参数 --key.value=xxx，然后是JVM命令行参数 -Dkey.value=xxx
		System.out.println("environment -> key.value:"+environment.getProperty("key.value"));
	}

}
