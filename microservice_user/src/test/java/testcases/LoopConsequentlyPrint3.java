package testcases;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoopConsequentlyPrint3 {
    private static volatile String code = "A";
    private static AtomicInteger count = new AtomicInteger(1);

    private static Lock lock = new ReentrantLock();

    private static Condition conditionA = lock.newCondition();
    private static Condition conditionB = lock.newCondition();
    private static Condition conditionC = lock.newCondition();


    public static void main(String[] args) throws Exception {

        Thread threadA = new Thread(() -> {
            while(true) {
                if(count.get()==10) break;
                lock.lock();
                try{
                    if ("A".equals(code)) {
                        System.out.println("线程A打印开始->"+count.getAndIncrement()+"次");
                        Thread.sleep(1000);

                        code = "B";
                        conditionB.signalAll();
                    }
                    else{
                        conditionA.await();
                        if(count.get()==10) break;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    lock.unlock();
                }
            }
            System.out.println(Thread.currentThread().getName() + " count:"+count.get());
        });

        Thread threadB = new Thread(() -> {
            while(true) {
                if(count.get()==10) break;
                lock.lock();
                try{
                    if ("B".equals(code)) {
                        System.out.println("线程B打印开始->"+count.getAndIncrement()+"次");
                        Thread.sleep(1000);

                        code = "C";
                        conditionC.signalAll();
                    }
                    else{
                        conditionB.await();
                        if(count.get()==10) break;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    lock.unlock();
                }
            }
            System.out.println(Thread.currentThread().getName() + " count:"+count.get());
        });

        Thread threadC = new Thread(() -> {
            while(true) {
                if(count.get()==10) break;
                lock.lock();
                try{
                    if ("C".equals(code)) {
                        System.out.println("线程C打印开始->"+count.getAndIncrement()+"次");
                        Thread.sleep(1000);

                        code = "A";
                        conditionA.signalAll();
                        conditionB.signalAll();
                    }
                    else{
                        conditionC.await();
                        if(count.get()==10) break;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    lock.unlock();
                }
            }
            System.out.println(Thread.currentThread().getName() + " count:"+count.get());
        });

        threadA.start();
        threadB.start();
        threadC.start();

    }
}
