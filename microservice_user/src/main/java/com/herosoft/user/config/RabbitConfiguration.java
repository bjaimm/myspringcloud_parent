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

    public static String DLK_EXCHANGE;

    public static String DLK_ROUTEKEY;

    public final static String DLK_QUEUE="dlkQueue";

    public  static String DEMO_EXCHANGE;

    public static String DEMO_ROUTEKEY;

    public static String DEMO_QUEUE;

    /**
     * 对于静态变量，@value需要加在set方法，且方法不能用static修饰符
     *
     * @param demoQueue
     */
    @Value("${demo.queue:demoQueue}")
    public void setDemoQueue(String demoQueue) {
        DEMO_QUEUE = demoQueue;
    }

    public static String DELAYPLUGIN_EXCHANGE;

    public static String DELAYPLUGIN_ROUTEKEY;

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

    /**
     * 对于静态变量，@value需要加在set方法，且方法不能用static修饰符
     *
     * @param dlkExchange
     *
     */
    @Value("${dlk.exchange:dlkExchange}")
    public void setDlkExchange(String dlkExchange) {
        DLK_EXCHANGE = dlkExchange;
    }

    @Value("${dlk.routekey:dlkRouteKey}")
    public void setDlkRoutekey(String dlkRoutekey) {
        DLK_ROUTEKEY = dlkRoutekey;
    }

    @Value("${demo.exchange:demoExchange}")
    public void setDemoExchange(String demoExchange) {
        DEMO_EXCHANGE = demoExchange;
    }

    @Value("${demo.routekey:demoRouteKey}")
    public void setDemoRoutekey(String demoRoutekey) {
        DEMO_ROUTEKEY = demoRoutekey;
    }

    @Value("${delayplugin.exchange:delayPluginExchange}")
    public void setDelaypluginExchange(String delaypluginExchange) {
        DELAYPLUGIN_EXCHANGE = delaypluginExchange;
    }

    @Value("${delayplugin.routekey:delayPluginRouteKey}")
    public void setDelaypluginRoutekey(String delaypluginRoutekey) {
        DELAYPLUGIN_ROUTEKEY = delaypluginRoutekey;
    }
}
