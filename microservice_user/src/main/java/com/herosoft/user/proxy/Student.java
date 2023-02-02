package com.herosoft.user.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Student implements Person{
    private String name;

    @Override
    public void wakeup() {
      log.info("学生[{}]早晨醒来。。。",getName());
    }

    @Override
    public void sleep() {
        log.info("学生[{}]晚上开始睡觉。。。",getName());
    }

    @Override
    public void eat(String food) {
        log.info("学生[{}]在吃{}。。。",getName(),food);
    }
}
