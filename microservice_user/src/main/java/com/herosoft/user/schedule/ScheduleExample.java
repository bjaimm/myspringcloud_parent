package com.herosoft.user.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.Date;

@Component
public class ScheduleExample {

    @Scheduled(cron = "0/5 * * * * ?")
    public void schedulePrint(){
        System.out.println("定时任务打印执行，当前时间："+(new Date()));
    }
}
