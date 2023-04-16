package com.herosoft.product.consumers;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.herosoft.commons.dto.message.OrderCreateMessageDto;
import com.herosoft.commons.dto.message.ProductReduceForOrderCreateDto;
import com.herosoft.product.config.RabbitMqConfig;
import com.herosoft.product.mappers.ProductChangeLogMapper;
import com.herosoft.product.po.ProductChangeLogPo;
import com.herosoft.product.service.ProductChangeLogService;
import com.herosoft.product.service.impl.ProductChangeLogServiceImpl;
import com.herosoft.product.service.impl.ProductServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class RabbitMqConsumer {
    @Autowired
    private ProductServiceImpl productServiceImpl;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ProductChangeLogMapper productChangeLogMapper;

    @Autowired
    private ProductChangeLogServiceImpl productChangeLogServiceImpl;

    @RabbitListener(queues = RabbitMqConfig.CREATE_ORDER_FOR_PRODUCT_QUEUE)
    @RabbitHandler
    public void onMessageCreateOrder(Object data, Channel channel, Message message) throws IOException {
        String msg = new String(message.getBody());
        boolean reduceSuccess=false;

        log.info("开始处理新建订单的消息-> {}",msg);
        OrderCreateMessageDto orderCreateMessageDto = JSON.parseObject(msg,OrderCreateMessageDto.class);

        //记录消息日志
        orderCreateMessageDto.getProductList().forEach(productDto -> {

            ProductChangeLogPo productChangeLogPo = new ProductChangeLogPo();

            int userId = orderCreateMessageDto.getUserId();
            int orderHeaderId = orderCreateMessageDto.getOrderHeaderId();
            int orderDetailId = orderCreateMessageDto.getOrderDetailId();

            productChangeLogPo.setOrderHeaderId(orderHeaderId);
            productChangeLogPo.setOrderDetailId(orderDetailId);
            productChangeLogPo.setProductId(productDto.getProductId());
            productChangeLogPo.setProductQty(productDto.getProductQty());
            productChangeLogPo.setStatus(0);
            productChangeLogPo.setCreateBy(userId);
            productChangeLogPo.setUpdateBy(userId);

            productChangeLogMapper.insert(productChangeLogPo);
        });

        //调用库存扣减
        try{
            reduceSuccess=productServiceImpl.reduceProductQtyByMessage(orderCreateMessageDto);
        }
        catch (RuntimeException e){

            log.info("库存扣减异常：{}",e.getLocalizedMessage());

        }

        //扣减不成功，更新消息日志状态
        if(!reduceSuccess){
            UpdateWrapper<ProductChangeLogPo> wrapper = new UpdateWrapper<>();
            ProductChangeLogPo productChangeLogPo = new ProductChangeLogPo();

            productChangeLogPo.setStatus(1);
            wrapper.lambda().set(ProductChangeLogPo::getStatus,1)
                    .set(ProductChangeLogPo::getUpdateTime, LocalDateTime.now())
                    .eq(ProductChangeLogPo::getOrderHeaderId,orderCreateMessageDto.getOrderHeaderId());
            //wrapper.eq("order_header_id",orderCreateMessageDto.getOrderHeaderId());
            productChangeLogServiceImpl.update(wrapper);
        }

        ProductReduceForOrderCreateDto productReduceMessage = new ProductReduceForOrderCreateDto();
        productReduceMessage.setOrderHeaderId(orderCreateMessageDto.getOrderHeaderId());
        productReduceMessage.setReduceProductSuccess(reduceSuccess);

        //库存扣减消息处理成功后，返回消息给订单服务修改订单状态
        rabbitTemplate.convertAndSend(RabbitMqConfig.PRODUCT_EXCHANGE,
                RabbitMqConfig.PRODUCT_REDUCE_FOR_CREATE_ORDER_ROUTE_KEY,
                productReduceMessage, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        return message;
                    }
                });

        //配置文件里设置了acknowledge-mode: manual，所以这里需要手动应答确认消费消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }
}
