package com.herosoft.user.config;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.converter.MessageConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitConfiguration配置了RabbitMQ交换机、队列及连接等
 *
 * @author HW
 * @date 2022/01/19
 */
@Configuration
@Slf4j
@PropertySource(value = "classpath:rabbitmq-cfg.properties")
public class RabbitConfiguration {

    @Value("${dlk.exchange}")
    public final static String DLK_EXCHANGE="dlkExchange";

    @Value("${dlk.routekey}")
    public final static String DLK_ROUTEKEY="dlkRouteKey";

    @Value("${dlk.queue}")
    public final static String DLK_QUEUE="dlkQueue";


    @Value("${demo.exchange}")
    public  final static String DEMO_EXCHANGE="demoExchange";

    @Value("${demo.routekey}")
    public final static String DEMO_ROUTEKEY="demoRouteKey";

    @Value("${demo.queue}")
    public final static String DEMO_QUEUE="demoQueue";


    @Value("${delayplugin.exchange}")
    public final static String DELAYPLUGIN_EXCHANGE="delayPluginExchange";

    @Value("${delayplugin.routekey}")
    public final static String DELAYPLUGIN_ROUTEKEY="delayPluginRouteKey";

    @Value("${delayplugin.queue}")
    public final static String DELAYPLUGIN_QUEUE="delayPluginQueue";

    @Bean
    public ConnectionFactory connectionFactory(@Value("${spring.rabbitmq.host}") String host,
                                               @Value("${spring.rabbitmq.port}")String port,
                                               @Value("${spring.rabbitmq.username}")String username,
                                               @Value("${spring.rabbitmq.password}")String password){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

        connectionFactory.setHost(host);
        connectionFactory.setPort(Integer.parseInt(port));
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        return connectionFactory.getRabbitConnectionFactory();

    }
    @Bean
    public DirectExchange dlkExchange(){
        return new DirectExchange(DLK_EXCHANGE,true,false);
    }

    @Bean
    public Queue dlkQueue(){
        return new Queue(DLK_QUEUE,true,false,false);
    }

    @Bean
    public Binding dlkBind(){
        return BindingBuilder.bind(dlkQueue()).to(dlkExchange()).with(DLK_ROUTEKEY);
    }

    @Bean
    public DirectExchange demoExchange(){
        return new DirectExchange(DEMO_EXCHANGE,true,false);
    }

    @Bean
    public Queue demoQueue(){
        Map<String,Object> map = new HashMap<>(1);

        map.put("x-dead-letter-exchange",DLK_EXCHANGE);
        map.put("x-dead-letter-routing-key",DLK_ROUTEKEY);

        return new Queue(DEMO_QUEUE,true,false,false,map);

    }

    @Bean
    public Binding demoBind(){
        return BindingBuilder.bind(demoQueue()).to(demoExchange()).with(DEMO_ROUTEKEY);
    }

    @Bean
    public CustomExchange delayPluginExchange(){
        Map<String,Object> args = new HashMap<>(1);

        args.put("x-delayed-type","direct");
        return new CustomExchange(DELAYPLUGIN_EXCHANGE,"x-delayed-message",true,false,args);
    }

    @Bean
    public Queue delayPluginQueue(){

        return new Queue(DELAYPLUGIN_QUEUE,true,false,false);

    }

    @Bean
    public Binding delayPluginBind(){
        return BindingBuilder.bind(delayPluginQueue()).to(delayPluginExchange()).with(DELAYPLUGIN_ROUTEKEY).noargs();
    }

    @Bean
    public Jackson2JsonMessageConverter createMessageConverter(){
        return  new Jackson2JsonMessageConverter();
    }

}
