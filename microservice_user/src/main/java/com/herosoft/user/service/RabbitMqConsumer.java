package com.herosoft.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.herosoft.user.config.RabbitConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMqConsumer {

    @RabbitHandler
    @RabbitListener(queues = RabbitConfiguration.DLK_QUEUE)
    public void onMessageDemo(Message message){
        log.info("使用死信队列，收到消息:{}",new String(message.getBody()));

        JSONObject jsonObject = JSON.parseObject(new String(message.getBody()));
        log.info("id:"+jsonObject.getString("id"));
        log.info("userName:"+jsonObject.getString("userName"));
        log.info("userType:"+jsonObject.getString("userType"));

        log.info(String.valueOf(message.getBody()));
    }

    @RabbitHandler
    @RabbitListener(queues = RabbitConfiguration.DELAYPLUGIN_QUEUE)
    public void onMessageDelayPlugin(Message message){
        log.info("使用延时插件队列，收到消息:{}",new String(message.getBody()));
    }
}
