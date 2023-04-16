package testcases;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class LoopConsequentlyPrint1 {
    private static volatile String code = "A";
    private static AtomicInteger count = new AtomicInteger(1);
    private static Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) {

        Thread threadA = new Thread(() -> {
            while(true) {
                if(count.get()==10) break;
                if (semaphore.tryAcquire()){

                    try {
                        if ("A".equals(code)) {
                            System.out.println("线程A打印开始->"+count.getAndIncrement()+"次");
                            Thread.sleep(1000);

                            code = "B";

                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    semaphore.release();
                }
            }
        });

        Thread threadB = new Thread(() -> {
            while(true) {
                if(count.get()==10) break;
                if (semaphore.tryAcquire()){

                    try {
                        if ("B".equals(code)) {
                            System.out.println("线程B打印开始->"+count.getAndIncrement()+"次");
                            Thread.sleep(1000);

                            code = "C";

                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    semaphore.release();
                }

            }
        });

        Thread threadC = new Thread(() -> {
            while(true) {
                if(count.get()==10) break;
                if (semaphore.tryAcquire()){

                    try{
                        if ("C".equals(code)) {
                            System.out.println("线程C打印开始->"+count.getAndIncrement()+"次");
                            Thread.sleep(1000);

                            code = "A";

                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    semaphore.release();
                }
            }
        });

        threadA.start();
        threadB.start();
        threadC.start();

    }
}
