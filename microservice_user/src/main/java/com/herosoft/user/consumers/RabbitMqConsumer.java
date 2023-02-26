package com.herosoft.user.consumers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.herosoft.commons.dto.message.OrderCreateMessageDto;
import com.herosoft.commons.dto.message.OrderPayMessageDto;
import com.herosoft.commons.dto.message.ProductReduceForOrderCreateDto;
import com.herosoft.user.config.RabbitConfiguration;
import com.herosoft.user.service.UserService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Component
@Slf4j
public class RabbitMqConsumer {

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitHandler
    @RabbitListener(queues = RabbitConfiguration.DLK_QUEUE)
    public void onMessageDemo(Object data, Channel channel, Message message) throws IOException {
        log.info("使用死信队列，收到消息:{}",new String(message.getBody()));

        JSONObject jsonObject = JSON.parseObject(new String(message.getBody()));
        log.info("id:"+jsonObject.getString("id"));
        log.info("userName:"+jsonObject.getString("userName"));
        log.info("userType:"+jsonObject.getString("userType"));

        log.info(String.valueOf(message.getBody()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @RabbitHandler
    @RabbitListener(queues = RabbitConfiguration.DELAYPLUGIN_QUEUE)
    public void onMessageDelayPlugin(Object data, Channel channel, Message message) throws IOException {
        log.info("使用延时插件队列，收到消息:{}",new String(message.getBody()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @RabbitHandler
    @RabbitListener(queues = RabbitConfiguration.PAY_ORDER_QUEUE)
    public void onMessagePayOrder(Object data, Channel channel, Message message) throws IOException {

        String msg = new String(message.getBody());
        log.info("开始处理支付订单的消息-> {}",msg);

        OrderPayMessageDto orderPayMessageDto = JSON.parseObject(msg, OrderPayMessageDto.class);

        int userId = orderPayMessageDto.getUserId();
        double orderAmount = orderPayMessageDto.getOrderAmount();

        log.info("账户余额扣减返回值：{}",userService.reduceBalance(userId,orderAmount));

        //库存扣减消息处理成功后，返回消息给订单服务修改订单状态
        rabbitTemplate.convertAndSend(RabbitConfiguration.USER_EXCHANGE,
                RabbitConfiguration.USER_ROUTEKEY,
                message, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        return message;
                    }
                });

        //配置文件里设置了acknowledge-mode: manual，所以这里需要手动应答确认消费消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
