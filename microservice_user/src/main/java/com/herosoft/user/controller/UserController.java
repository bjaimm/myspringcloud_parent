package com.herosoft.user.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.herosoft.commons.annotations.NotControllerResponseAdvice;
import com.herosoft.commons.dto.UserDto;
import com.herosoft.commons.enums.ResponseEnum;
import com.herosoft.commons.exceptions.DefinitionException;
import com.herosoft.commons.results.Result;
import com.herosoft.user.async.AsyncService;
import com.herosoft.user.events.ObservationEvent;
import com.herosoft.user.po.UserPo;
import com.herosoft.user.service.RabbitMqSender;
import com.herosoft.user.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@RestController
@RefreshScope //设置刷新配置后，不重启微服务
@RequestMapping(value = "/users")
@Api(value = "用户管理接口")
public class UserController {
    /**
     * lock retry time
     */
    private final static Integer LOCK_RETRY_TIME=5;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private RabbitMqSender rabbitMqSender;

    @Autowired
    private Environment environment;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private ApplicationContext applicationContext;

    private static Map<String,String> staticMap = new HashMap<>();
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
    @GetMapping("/deadLock/{id}")
    public String deadLock(@PathVariable int id){
        while(id==1){
            System.out.println("这是一个死循环。。。");
        }
        return "死循环结束了。。。";
    }
    @GetMapping("/countTest")
    @ApiOperation(value = "计数器")
    public String countTest(){
        count++;

        staticMap.put(String.valueOf(count),"count"+count);

        for(Map.Entry<String,String> entry: staticMap.entrySet()){
            System.out.println("key:"+entry.getKey()+"  value:"+entry.getValue());
        };
        return String.valueOf(count);
    }

    public void threadMethod(Thread thread) throws InterruptedException {
        ///lock() method only try to get lock for once
        //lock.lock();
        if(lock.tryLock(LOCK_RETRY_TIME,TimeUnit.SECONDS)) {
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

    @NotControllerResponseAdvice
    @RequestMapping(value = "/checkstatus",method = RequestMethod.GET)
    public String getStatus(){

        return "用户微服务状态正常 Port:"+environment.getProperty("local.server.port")+" token secret:"+environment.getProperty("token.secret");
    }
    @RequestMapping(value = "/userhandler/{userType}")
    public String userHandler(@PathVariable String userType){
        UserDto userDto = new UserDto();
        userDto.setUserId(1);
        userDto.setUserName("普通用户");
        userDto.setUserType(userType);

        return userServiceImpl.handler(userDto);

    }
    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> findAllUsers(){

        List<UserDto> userList = Optional.ofNullable(userServiceImpl.findAll())
                .map(list -> list.stream()
                        .map(userPo ->{
                            UserDto user = new UserDto();

                            user.setUserId(userPo.getId());
                            user.setUserName(userPo.getUsername());
                            user.setBalance(userPo.getBalance());
                            user.setPassword(userPo.getPassword());
                            user.setSex(userPo.getSex());
                            user.setUserType("1");
                            user.setCreateDt(userPo.getCreatedt());
                            user.setUpdateDt(userPo.getUpdatedt());
                            return user;})
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<UserDto>());

        return userList;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody @Valid UserPo user){
        userServiceImpl.add(user);

        return new Result<>(true, ResponseEnum.SUUCESS.getReponseCode(), ResponseEnum.SUUCESS.getReponseMessage(), "添加用户成功");
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto findById(@PathVariable  Integer id){
        System.out.println("正在查询用户。。。");
        UserDto userDto = Optional.ofNullable(userServiceImpl.findById(id))
                .map( userPo -> {
                    UserDto user = new UserDto();
                    user.setUserId(userPo.getId());
                    user.setUserName(userPo.getUsername());
                    user.setBalance(userPo.getBalance());
                    user.setPassword(userPo.getPassword());
                    user.setSex(userPo.getSex());
                    user.setUserType("1");
                    user.setCreateDt(userPo.getCreatedt());
                    user.setUpdateDt(userPo.getUpdatedt());
                    return user;
                }).orElse(new UserDto());

        return userDto;
    }

    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
    public String deleteById(@PathVariable  Integer id){
        userServiceImpl.delete(id);
        return "删除成功";
    }

    @GetMapping(value = "/asyncCall")
    public String asyncCall(){
        long startTime = System.currentTimeMillis();

        asyncService.helloAsync();
        long endTime = System.currentTimeMillis();
        System.out.println("Controller asyncCall执行用时:"+(endTime-startTime)+"毫秒");
        return "Success";

    }
    @GetMapping("/getDefineException")
    public Result getDefineException(){
        throw new DefinitionException(400,"我出错了");
    }

    @GetMapping("/getOtherException")
    public Result getOtherException(){
        int temp = 10/0;
        return new Result<>();
    }

    @GetMapping("/rabbitmq/dlkSend")
    public Result dlkSend(@RequestParam String message, Integer delay){
        UserDto userDto = new UserDto();
        userDto.setUserId(0);
        userDto.setUserName(message);
        userDto.setUserType("1");


        rabbitMqSender.sendObjectDemo(userDto,delay);
        return new Result<>(true,
                ResponseEnum.SUUCESS.getReponseCode(),
                ResponseEnum.SUUCESS.getReponseMessage(),
                "使用死信队列发送消息成功");
    }

    @GetMapping("/rabbitmq/delayPluginSend")
    public Result delayPluginSend(@RequestParam String message, Integer delay){
        rabbitMqSender.sendDelayPlugin(message,delay);
        return new Result<>(true,
                ResponseEnum.SUUCESS.getReponseCode(),
                ResponseEnum.SUUCESS.getReponseMessage(),
                "使用延时插件队列发送消息成功");
    }

    @GetMapping("/publishevent/{message}")
    public Result publishEvent(@PathVariable String message){
        applicationContext.publishEvent(new ObservationEvent(message));
        return new Result<>(true,
                ResponseEnum.SUUCESS.getReponseCode(),
                ResponseEnum.SUUCESS.getReponseMessage(),
                "使用ApplicatonContext发送Event消息成功");
    }

    @RequestMapping(method = RequestMethod.PUT)
    Result updateBalance(@RequestBody UserDto userDto){
        System.out.println("用户服务Controller开始执行余额扣除。。。");
        Integer userId = userDto.getUserId();

        UserPo user = userServiceImpl.findById(userId);

        UpdateWrapper<UserPo> userUpdateWrapper = new UpdateWrapper<>();

        user.setBalance(user.getBalance()-userDto.getBalance());
        userUpdateWrapper.eq("id", userId);

        userServiceImpl.update(user,userUpdateWrapper);

        return Result.success("成功更新账户余额");
    }
}
