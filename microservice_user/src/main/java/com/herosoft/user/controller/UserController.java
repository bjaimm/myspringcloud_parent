package com.herosoft.user.controller;

import com.herosoft.user.pojo.User;
import com.herosoft.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@RestController
@RefreshScope //设置刷新配置后，不重启微服务
@RequestMapping(value = "/users")
@Api(value = "用户管理接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private Environment environment;

    private Integer count=0;
    private Lock lock=new ReentrantLock();

    @GetMapping("/kafka/{topic}/{message}")
    public void sendMessage1(@PathVariable String topic,@PathVariable String message){
        kafkaTemplate.send(topic,message);
        kafkaTemplate.send(topic,0,"key",message);

    }

    @GetMapping("/kafkacallback/{topic}/{message}")
    @Transactional(rollbackFor = RuntimeException.class)
    public void sendMessage2(@PathVariable String topic,@PathVariable String message){
        kafkaTemplate.send(topic,message).addCallback(new ListenableFutureCallback<SendResult<String,Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("发送消息失败，exception:"+throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String,Object> result) {
                System.out.println("发送消息成功, Topic:"+result.getRecordMetadata().topic()+" Partition:"+result.getRecordMetadata().partition()+" Offset:"+result.getRecordMetadata().offset());
            }
        });
    }

    @GetMapping("/kafkacallback2/{topic}/{message}")
    @Transactional(rollbackFor = RuntimeException.class)
    public void sendMessage3(@PathVariable String topic,@PathVariable String message){
        ListenableFuture<SendResult<String,Object>> listenableFuture=kafkaTemplate.send(topic,message);
        listenableFuture.addCallback(success -> {

            System.out.println("发送消息成功, Message:"+success.getProducerRecord().value()+" Topic:"+success.getRecordMetadata().topic()+" Partition:"+success.getRecordMetadata().partition()+" Offset:"+success.getRecordMetadata().offset());

        },failure -> {
            System.out.println("发送消息失败，exception:"+failure.getMessage());
        });
    }

    @GetMapping("/countTest")
    @ApiOperation(value = "计数器")
    public String countTest(){
        count++;
        return String.valueOf(count);
    }

    public void threadMethod(Thread thread) throws InterruptedException {
        //lock.lock();
        if(lock.tryLock(5,TimeUnit.SECONDS)) {
            try {
                System.out.println("线程" + thread.getName() + "执行中。。。");
                Thread.sleep(3000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            } finally {
                lock.unlock();
                System.out.println("线程" + thread.getName() + "结束。。。");
            }
        }
    }

    @RequestMapping(value = "/checkstatus",method = RequestMethod.GET)
    public String getStatus(){

        return "用户微服务状态正常 Port:"+environment.getProperty("local.server.port")+" token secret:"+environment.getProperty("token.secret");
    }
    @RequestMapping(method = RequestMethod.GET)
    public List<User> findAllUsers(){

        List<User> userList = new ArrayList<User>();

        /*userList.add(new User(1,"张三","123456","男",2000.0));
        userList.add(new User(2,"李四","123456","男",3000.0));
        userList.add(new User(3,"王五","123456","女",4000.0));
        */

        userList=userService.findAll();
        //模拟服务调用延时
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userList=userList.stream().filter((m)-> m.getBalance()<3000.0).collect(Collectors.toList());
        //return userList;
        return userList;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(@RequestBody User user){
        userService.add(user);
        return "添加用户成功";
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public User findById(@PathVariable  Integer id){
        System.out.println("正在查询用户。。。");
        return userService.findById(id);
    }

    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
    public String deleteById(@PathVariable  Integer id){
        userService.delete(id);
        return "删除成功";
    }
}
