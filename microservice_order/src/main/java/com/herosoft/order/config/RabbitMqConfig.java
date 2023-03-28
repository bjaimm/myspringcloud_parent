package com.herosoft.order.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * RabbitConfiguration配置了RabbitMQ交换机、队列及连接等
 *
 * @author HW
 * @date 2022/01/19
 */
@Configuration
@Slf4j
@PropertySource(value = "classpath:rabbitmq-cfg.properties")
public class RabbitMqConfig {
    public final static String PRODUCT_REDUCE_FOR_CREATE_ORDER_QUEUE="prouctReduceForCreateOrderQueue";

    public final static String BALANCE_REDUCE_FOR_PAY_ORDER_QUEUE="balanceReduceForPayOrderQueue";

    public  static String ORDER_EXCHANGE;

    public static String ORDER_CREATE_ROUTEKEY;

    public static String ORDER_PAY_ROUTEKEY;

    //SpringBoot 会根据application.yml的设置自动生成ConnectionFactory实例，这里是手动生成的例子
    /*@Bean
    public ConnectionFactory connectionFactory(@Value("${spring.rabbitmq.host}") String host,
                                               @Value("${spring.rabbitmq.port}")String port,
                                               @Value("${spring.rabbitmq.username}")String username,
                                               @Value("${spring.rabbitmq.password}")String password){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

        connectionFactory.setHost(host);
        connectionFactory.setPort(Integer.parseInt(port));
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);

        return connectionFactory;

    }*/

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

    /**
     * 对于静态变量，@value需要加在set方法，且方法不能用static修饰符
     *
     * @param
     *
     */
    @Value("${order.exchange}")
    public void setOrderExchange(String orderExchange) {
         ORDER_EXCHANGE = orderExchange;
    }

    @Value("${order.create.routekey}")
    public void setOrderCreateRouteKey(String orderCreateRouteKey) {
        ORDER_CREATE_ROUTEKEY = orderCreateRouteKey;
    }

    @Value("${order.pay.routekey}")
    public void setOrderPayRouteKey(String orderPayRouteKey) {
        ORDER_PAY_ROUTEKEY = orderPayRouteKey;
    }

}
