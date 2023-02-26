package com.herosoft.order.consumers;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.herosoft.commons.dto.message.OrderPayMessageDto;
import com.herosoft.commons.dto.message.ProductReduceForOrderCreateDto;
import com.herosoft.order.config.RabbitMqConfig;
import com.herosoft.order.po.OrderHeaderPo;
import com.herosoft.order.services.impl.OrderHeaderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class RabbitConsumers {

    @Autowired
    private OrderHeaderServiceImpl orderHeaderServiceImpl;

    @RabbitListener(queues = RabbitMqConfig.PRODUCT_REDUCE_FOR_CREATE_ORDER_QUEUE)
    @RabbitHandler
    public void onMessageProductReduceForOrderCreate(Message message){
        log.info("开始处理库存扣减服务返回的消息。。。{}",(new String(message.getBody())));
        ProductReduceForOrderCreateDto productReduceMessage = JSON.parseObject(new String(message.getBody()), ProductReduceForOrderCreateDto.class);

        OrderHeaderPo orderHeaderPo = new OrderHeaderPo();
        UpdateWrapper<OrderHeaderPo> orderHeaderWrapper = new UpdateWrapper<>();

        if(!productReduceMessage.isReduceProductSuccess()){
            orderHeaderPo.setStatus(3);
        }
        else{
            orderHeaderPo.setStatus(1);
        }
        orderHeaderWrapper.eq("order_header_id",productReduceMessage.getOrderHeaderId())
                .eq("status",0);

        orderHeaderServiceImpl.update(orderHeaderPo,orderHeaderWrapper);

    }

    @RabbitListener(queues = RabbitMqConfig.BALANCE_REDUCE_FOR_PAY_ORDER_QUEUE)
    @RabbitHandler
    public void onMessageBalanceReduceForPayOrder(Message message){
        log.info("开始处理账户扣减余额服务返回的消息。。。{}",(new String(message.getBody())));
        OrderPayMessageDto orderPayMessageDto = JSON.parseObject(new String(message.getBody()), OrderPayMessageDto.class);

        OrderHeaderPo orderHeaderPo = new OrderHeaderPo();
        UpdateWrapper<OrderHeaderPo> orderHeaderWrapper = new UpdateWrapper<>();

        orderHeaderPo.setStatus(2);
        orderHeaderWrapper.eq("order_header_id",orderPayMessageDto.getOrderHeaderId())
                .in("status", Arrays.asList(0,1));

        orderHeaderServiceImpl.update(orderHeaderPo,orderHeaderWrapper);

    }

}
