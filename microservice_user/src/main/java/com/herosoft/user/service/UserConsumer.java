package com.herosoft.user.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserConsumer {

    @KafkaListener(topics = {"userTopic4"})
    public void listener(ConsumerRecord consumerRecord){
        Optional msg = Optional.ofNullable(consumerRecord.value());
        if(msg.isPresent()){
            System.out.println(String.format("key: %s  value: %s partition: %s",consumerRecord.key(),consumerRecord.value(),consumerRecord.partition()));
        }
    }
}
