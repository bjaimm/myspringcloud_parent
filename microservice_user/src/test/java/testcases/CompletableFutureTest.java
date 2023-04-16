package testcases;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureTest {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CompletableFuture<String> cfSohu = CompletableFuture.supplyAsync(CompletableFutureTest :: queryStockFromSohu);

        CompletableFuture<String> cfSina = CompletableFuture.supplyAsync(CompletableFutureTest :: queryStockFromSina);

        CompletableFuture<Object> cf = CompletableFuture.anyOf(cfSina,cfSohu);

        cf.thenAccept((code)->{
            System.out.println("获取到的股票代码为"+code);
        });

        cf.exceptionally((e)->{
            System.out.println("获取股票代码异常："+e.getLocalizedMessage());
            return null;
        });
        cf.get();
        //为了测试效果，主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭
        //Thread.sleep(2000);
    }

    public static String queryStockFromSohu() {
        try {
            System.out.println("正在从搜狐网获取股票代码。。。");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(Math.random()<0.9) {
            return "600600(搜狐网)";
        }
        else {
            throw new RuntimeException("搜狐网股票服务器挂了");
        }
    }

    public static String queryStockFromSina() {
        try {
            System.out.println("正在从新浪网获取股票代码。。。");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(Math.random()<0.9) {
            return "600600(新浪网)";
        }
        else {
            throw new RuntimeException("新浪网股票服务器挂了");
        }
    }
}
