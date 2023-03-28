package com.herosoft.user;

import com.herosoft.user.proxy.JdkDynamicProxy;
import com.herosoft.user.proxy.Person;
import com.herosoft.user.proxy.StudentStaticProxy;
import com.herosoft.user.proxy.Student;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.lang.reflect.Proxy;

@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
@EnableAsync
@EnableTransactionManagement
@EnableRetry
public class UserApplication {

    public static void main(String[] args) {

        SpringApplication.run(UserApplication.class,args);
        System.out.println("UserApplicaiton main方法开始执行。。。");
        //创建被代理对象
        Person student = new Student("张三");
        //创建动态代理
        JdkDynamicProxy proxy = new JdkDynamicProxy(student);

        //创建代理对象实列
        Person studentDynamicProxy = (Person) Proxy.newProxyInstance(proxy.getClass().getClassLoader(),
                //new Class[]{Person.class},
                student.getClass().getInterfaces(),
                proxy);

        studentDynamicProxy.wakeup();
        studentDynamicProxy.sleep();
        studentDynamicProxy.eat("苹果");

        Person studentStaticProxy = new StudentStaticProxy(new Student("李四"));

        studentStaticProxy.wakeup();
        studentStaticProxy.sleep();
        studentStaticProxy.eat("苹果");


/*
        List<String> topics = new ArrayList<>();
        Map<String, Object> kafkaProps = new HashMap<>();

        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  "kafka:9092");
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, "microservice-user-group");
        kafkaProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        KafkaConsumer<String,String> kafkaConsumer = new KafkaConsumer<>(kafkaProps);

        topics.add("userTopic3");
        //kafkaConsumer.subscribe(topics);//订阅主题
        //kafkaConsumer.assign(Arrays.asList(new TopicPartition("userTopic3",0)));//订阅单一主题分区
        List<TopicPartition> list = new ArrayList<>();

        List<PartitionInfo> partitionInfos= kafkaConsumer.partitionsFor("userTopic4");

        for(PartitionInfo partitionInfo:partitionInfos){
            System.out.println(partitionInfo.topic()+" : "+partitionInfo.partition());
            list.add(new TopicPartition(partitionInfo.topic(),partitionInfo.partition()));
        }

        for(TopicPartition topicPartition:list){
            kafkaConsumer.assign(Arrays.asList(topicPartition));
        }
        try{
            while (true){
                ConsumerRecords<String,String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(5));
                for(ConsumerRecord<String,String> consumerRecord:consumerRecords){
                    System.out.println(String.format("key: %s  value: %s",consumerRecord.key(),consumerRecord.value()));
                }
            }

        }
        finally {
            kafkaConsumer.close();
        }*/

/*
        UserController userController = new UserController();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    userController.threadMethod(Thread.currentThread());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"Thread1");

        Thread thread2= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    userController.threadMethod(Thread.currentThread());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"Thread2");

        thread1.start();
        thread2.start();

        //匿名内部继承线程类
        new Thread(){
            private String name ="马云";
            private Integer ticketCount=20;

            @Override
            public  void  run(){
                while(ticketCount>0){
                    System.out.println(name+"正在买票，剩余"+ticketCount+"张");
                    ticketCount--;
                }
            }
        }.start();

        ExtendedThread extendedThread = new ExtendedThread("马化腾");
        extendedThread.start();

        RunnableThread runnableThread = new RunnableThread(10);
        new Thread(runnableThread,"张三").start();
        new Thread(runnableThread,"李四").start();

        long startTime = System.currentTimeMillis();
        PingIp pingIp = new PingIp();

        for(int i=1;i<=2;i++){
            pingIp.ping("192.168.0."+i);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("单线程用时："+(endTime-startTime)/1000);

        long startTime2 = System.currentTimeMillis();
        System.out.println("当前线程名称："+Thread.currentThread().getName());
        for(int i=3;i<=4;i++){
            new MultiThreadPing("192.168.0."+i).start();
        }

        long endTime2 = System.currentTimeMillis();
        System.out.println("多线程用时："+(endTime2-startTime2)/1000);

        RunnableThread runnableThread1=new RunnableThread(10000);

        ExecutorService excutorService= Executors.newFixedThreadPool(4);
        excutorService.execute(runnableThread1);

        //Yield thread demo
        YieldThread yieldThread = new YieldThread();
        Thread thread1 = new Thread(yieldThread,"张三吃完还剩");
        Thread thread2 = new Thread(yieldThread,"李四吃完还剩");
        Thread thread3 = new Thread(yieldThread,"王五吃完还剩");

        thread1.start();
        thread2.start();
        thread3.start();

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for(int i =1;i<=10;i++){
            executorService.execute(()->{System.out.println(Thread.currentThread().getName()+"正在执行任务");});
        }*/

    }

}
