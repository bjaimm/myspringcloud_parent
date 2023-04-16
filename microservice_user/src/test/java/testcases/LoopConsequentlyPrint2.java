package testcases;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class LoopConsequentlyPrint2 {
    private static volatile String code = "A";
    private static AtomicInteger count = new AtomicInteger(1);

    private static Object lock= new Object();

    public static void main(String[] args) throws Exception {

        Thread threadA = new Thread(() -> {
            while(true) {
                if(count.get()==10) break;
                synchronized (lock){

                    try {
                        if ("A".equals(code) ) {
                            System.out.println("线程A打印开始->"+count.getAndIncrement()+"次");
                            Thread.sleep(1000);

                            code = "B";
                            lock.notifyAll();
                        }
                        else{
                            lock.wait();
                            if(count.get()==10) break;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            System.out.println(Thread.currentThread().getName() + " count:"+count.get());
        });

        Thread threadB = new Thread(() -> {
            while(true) {
                if(count.get()==10) break;
                synchronized (lock){

                    try {
                        if ("B".equals(code) ) {
                            System.out.println("线程B打印开始->"+count.getAndIncrement()+"次");
                            Thread.sleep(1000);

                            code = "C";
                            lock.notifyAll();
                        }
                        else{
                            lock.wait();
                            if(count.get()==10) break;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
            System.out.println(Thread.currentThread().getName() + " count:"+count.get());
        });

        Thread threadC = new Thread(() -> {
            while(true) {
                if(count.get()==10) break;
                synchronized (lock){

                    try{
                        if ("C".equals(code)&&count.get()<10) {
                            System.out.println("线程C打印开始->"+count.getAndIncrement()+"次");
                            Thread.sleep(1000);

                            code = "A";
                            lock.notifyAll();
                        }
                        else{
                            lock.wait();
                            if(count.get()==10) break;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
            System.out.println(Thread.currentThread().getName() + " count:"+count.get());
        });

        threadA.start();
        threadB.start();
        threadC.start();

    }
}
