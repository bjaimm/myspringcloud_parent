package com.herosoft.movie.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient getRedissonClient(){
        Config config = new Config();

        config.useSingleServer().setAddress("redis://localhost:6379")
                .setDatabase(0).setConnectionMinimumIdleSize(1000)
                .setConnectionPoolSize(2000).setPingConnectionInterval(60);
        return Redisson.create(config);
    }
}
