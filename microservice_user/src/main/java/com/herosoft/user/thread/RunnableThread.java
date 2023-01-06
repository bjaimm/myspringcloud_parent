package com.herosoft.user.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RunnableThread implements Runnable{
    public RunnableThread( Integer ticketCount) {
        this.ticketCount = ticketCount;
    }
    private Integer ticketCount;

    private Lock lock = new ReentrantLock();

    @Override
    public void run() {
        while(ticketCount>0){

            System.out.println(Thread.currentThread().getName()+"正在卖票，还剩余"+ticketCount+"张");
            ticketCount--;

        }
    }
}
