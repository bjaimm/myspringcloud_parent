package com.herosoft.user.thread;

public class ExtendedThread extends Thread{
    private String name;

    private Integer ticketCount=20;

    @Override
    public void run() {
        while(ticketCount>0){
            System.out.println(Thread.currentThread().getName()+":"+name+"正在买票，还剩"+ticketCount+"张");
            ticketCount--;
        }
    }

    public ExtendedThread(String name) {
        this.name = name;
    }


}
