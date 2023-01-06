package com.herosoft.user.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfiguration {

    private static final Map<String, Object> kafkaProps = new HashMap<>();

    private static String ip;

    private static String port;

    private static String groupId;

    private static String autoCommit;

    private static String autoOffsetReset;

    @Value("${network.kafka.ip}")
    public void setIp(String networkIp) {
        ip = networkIp;
    }

    @Value("${network.kafka.port}")
    public void setPort(String networkPort) {
        port = networkPort;
    }

    @Value("${spring.kafka.consumer.group-id}")
    public void setGroupId(String consumerGroupId) {
        groupId = consumerGroupId;
    }

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    public void setAutoCommit(String consumerAutoCommit) {
        autoCommit = consumerAutoCommit;
    }

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    public void setAutoOffsetReset(String offsetReset) {
        autoOffsetReset = offsetReset;
    }

    /**
     * 初始化consumer config
     */
    public static void initConsumer() {
        //连接地址
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ip + ":" + port);
        //GroupID
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //是否自动提交
        kafkaProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        //键的反序列化方式
        kafkaProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //值的反序列化方式
        kafkaProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // 从何处开始消费,latest 表示消费最新消息,earliest 表示从头开始消费,none表示抛出异常,默认latest
        kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
    }

    @Bean
    public KafkaConsumer<String, String> getKafkaConsumer() {
        initConsumer();
        return new KafkaConsumer<>(kafkaProps);
    }

}
