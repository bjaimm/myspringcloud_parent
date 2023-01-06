package com.herosoft.user.thread;

public class YieldThread implements Runnable{
    private int count=20;

    @Override
    public void run() {
        while(count>0){
            System.out.println(Thread.currentThread().getName()+ count--  +"个瓜");
            if(count% 2 ==0){
                Thread.yield();
            }
        }
    }
}
