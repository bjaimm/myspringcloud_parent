package com.herosoft.user.config;

import com.herosoft.user.listeners.ObservationListenerA;
import com.herosoft.user.listeners.ObservationListenerB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ObservationConfig {

    @Bean
    public ObservationListenerA observationListenerA(){
        return new ObservationListenerA("订单业务");
    }

    @Bean
    public ObservationListenerB observationListenerB(){
        return new ObservationListenerB("积分扣减业务");
    }
}
