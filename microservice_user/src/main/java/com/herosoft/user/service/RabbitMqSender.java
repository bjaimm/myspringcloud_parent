package com.herosoft.user.service;

import com.herosoft.user.config.RabbitConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMqSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendDemo(String message,Integer delay){
        String ttl=String.valueOf(delay*1000);

        rabbitTemplate.convertAndSend(RabbitConfiguration.DEMO_EXCHANGE, RabbitConfiguration.DEMO_ROUTEKEY, message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(ttl);
                return message;
            }
        });

        log.info("利用死信队列实现的延时消息已发送，消息:{},延时:{}秒",message,delay);
    }

    public void sendObjectDemo(Object message,Integer delay){
        String ttl=String.valueOf(delay*1000);

        rabbitTemplate.convertAndSend(RabbitConfiguration.DEMO_EXCHANGE, RabbitConfiguration.DEMO_ROUTEKEY, message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(ttl);
                return message;
            }
        });

        log.info("利用死信队列实现的延时消息已发送，消息:{},延时:{}秒",message,delay);
    }

    public void sendDelayPlugin(String message,Integer delay){

        rabbitTemplate.convertAndSend(RabbitConfiguration.DELAYPLUGIN_EXCHANGE, RabbitConfiguration.DELAYPLUGIN_ROUTEKEY, message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setHeader("x-delay",delay*1000);
                return message;
            }
        });

        log.info("利用延时队列插件实现的延时消息已发送，消息:{},延时:{}秒",message,delay);
    }
}
