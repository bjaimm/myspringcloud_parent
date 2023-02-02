package com.herosoft.user.proxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StudentStaticProxy implements Person{

    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public StudentStaticProxy(Student student) {
        this.student = student;
    }

    @Override
    public void wakeup() {
        log.info("静态代理：早安啊。。。");
        this.student.wakeup();
    }

    @Override
    public void sleep() {
        log.info("静态代理：晚安啊。。。");
        this.student.sleep();
    }

    @Override
    public void eat(String food) {
        log.info("静态代理：吃{}吧。。。",food);
        this.student.eat(food);
    }
}
