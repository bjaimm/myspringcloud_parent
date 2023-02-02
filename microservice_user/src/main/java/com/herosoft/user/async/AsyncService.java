package com.herosoft.user.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * AsyncService class
 *
 * @author HanWei
 * @date 2023/01/18
 */
@Component
public class AsyncService {

    @Async
    public void helloAsync(){
        try{
            Thread.sleep(10000);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("异步业务代码执行完毕。");
    }
}
