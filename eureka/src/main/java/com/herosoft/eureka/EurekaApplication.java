package com.herosoft.eureka;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class,args);

        String str = "This is the first array for Lambda";
        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<String>();
        list1.add("tst");
        String o = list1.get(0);
        String cellphone = "234324";

        if(cellphone.matches("\\d{6}")){
            System.out.println("手机号格式正确!");
        }
        else{
            System.out.println("手机号格式不正确");
        }
        JSONObject jsonObject = JSON.parseObject("{'username':'hanwei','age':'46'}");
        System.out.println("jsonObject username:"+jsonObject.get("username"));
        System.out.println("jsonObject age:"+jsonObject.get("age"));

        Thread thread = new Thread(()->{
            System.out.println("Hello from thread");
        });
        System.out.println("start");
        thread.start();
        /*try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        System.out.println("end");

        //策略设计模式演示 Arrays.sort(arg1, arg2); arg2为不同策略
        String[] arrays = {"apple","Orange","Banana","Pear"};
        Arrays.sort(arrays,String::compareToIgnoreCase);
        System.out.println(Arrays.toString(arrays));
        Arrays.sort(arrays,(s1,s2)->s1.compareTo(s2));
        System.out.println(Arrays.toString(arrays));

        list=Arrays.asList("1","2");
        for (String item:list ) {
            System.out.println(item);
        }

        Arrays.stream(str.split(" ")).map(s->s.length()).forEach(System.out::println);

        ThreadDemo thread1 = new ThreadDemo();

        new Thread(thread1).start();

        while(true){
            synchronized (thread1) {
                if (thread1.isFlag()) {
                    System.out.println("主线程获取到Flag为True");
                    break;
                }
            }
        }
        Service service = new Service();
        ThreadA threadA = new ThreadA(service);
        ThreadB threadB = new ThreadB(service);
        threadA.setName("ThreadA");
        threadB.setName("ThreadB");
        threadA.start();
        threadB.start();
    }
}

class ThreadDemo implements Runnable{
    private boolean flag =false;

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        flag=true;
        System.out.println("Flag is "+isFlag());
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}

class Service {
    Object lock = new Object();

    public void waitMethod(){
        synchronized (lock){
            try {
                System.out.println(Thread.currentThread().getName()+"执行了Wait方法，释放了锁");
                lock.wait();
                System.out.println(Thread.currentThread().getName()+"被唤醒了，继续执行线程逻辑");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void notifyMethod(){
        synchronized (lock){
            System.out.println(Thread.currentThread().getName()+"执行了Notify方法");
            lock.notify();
            System.out.println(Thread.currentThread().getName()+"继续执行完线程逻辑，退出sychronized block，再释放锁");

        }
    }
}
class ThreadA extends Thread{
    private Service service;

    public ThreadA(Service service){
        this.service=service;
    }

    @Override
    public void run(){
        super.run();
        service.waitMethod();
    }
}
class ThreadB extends Thread{
    private Service service;

    public ThreadB(Service service){
        this.service=service;
    }

    @Override
    public void run(){
        super.run();
        service.notifyMethod();
    }
}
