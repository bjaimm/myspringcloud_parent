import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;

public class TestThreadPool {

    @Test
    public void testCacheThreadPool() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (int i = 1; i <= 10; i++) {
            final int index = 1;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("CacheThreadPool " + Thread.currentThread().getName() + "线程正在执行-> index=" + index);
                }
            });
        }
    }

    @Test
    public void testFixedThreadPool() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for (int i = 1; i <= 10; i++) {
            final int index = 1;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("FixedThreadPool " + Thread.currentThread().getName() + "线程正在执行-> index=" + index);
                }
            });
        }
    }

    @Test
    public void testSingleThreadPool() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        for (int i = 1; i <= 10; i++) {
            final int index = 1;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("SingleThreadPool " + Thread.currentThread().getName() + "线程正在执行-> index=" + index);
                }
            });
        }
    }

    @Test
    public void testScheduleThreadPool() {
        ExecutorService executorService = Executors.newScheduledThreadPool(2);

        for (int i = 1; i <= 10; i++) {
            final int index = 1;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("ScheduleThreadPool " + Thread.currentThread().getName() + "线程正在执行-> index=" + index);
                }
            });
        }
    }

    @Test
    public void testScheduleThreadPool2() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);

        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("ScheduleThreadPool " + Thread.currentThread().getName() + "线程正在执行..." );
            }
        }, 3, TimeUnit.SECONDS);

    }

    @Test
    public void testThreadPoolExecutor(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,
                4,
                300,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4));
    }

    public static void main(String[] args) {
        System.out.println("Main method 开始执行。。。");
    }
}
