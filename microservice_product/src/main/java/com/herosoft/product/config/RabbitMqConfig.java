package com.herosoft.product.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:rabbitmq-cfg.properties")
@Slf4j
public class RabbitMqConfig {
    public final static String CREATE_ORDER_FOR_PRODUCT_QUEUE="createOrderForProductService";

    public static String PRODUCT_EXCHANGE;

    public static String PRODUCT_REDUCE_FOR_CREATE_ORDER_ROUTE_KEY;

    @Value("${product.exchange}")
    public void setProductExchage(String productExchange) {
        PRODUCT_EXCHANGE = productExchange;
    }
    @Value("${product.reduce.for.create.order.routekey}")
    public void setProductReduceForCreateOrderRouteKey(String productReduceForCreateOrderRouteKey) {
        PRODUCT_REDUCE_FOR_CREATE_ORDER_ROUTE_KEY = productReduceForCreateOrderRouteKey;
    }

    @Bean
    public Jackson2JsonMessageConverter createMessageConverter(){
        return  new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){

        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(createMessageConverter());

        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (ack) {
                    log.info("ConfirmCallback 关联数据{},投递成功,确认情况{}",correlationData,ack);
                }
                else {
                    log.info("ConfirmCallback 关联数据{},投递失败,确认情况{},失败原因{}",correlationData,ack,cause);
                }
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                log.info("ReturnCallback message:{},i:{},Cause:{},Exchange:{},RouteKey:{}",new String(message.getBody()),i,s,s1,s2);
            }
        });
        return rabbitTemplate;
    }
}
