package com.herosoft.user.thread;

import com.herosoft.user.test.PingIp;

public class MultiThreadPing extends Thread{
    private String ip;

    public MultiThreadPing(String ip) {
        this.ip = ip;
    }

    @Override
    public void run() {
        PingIp.ping(ip);
    }
}
