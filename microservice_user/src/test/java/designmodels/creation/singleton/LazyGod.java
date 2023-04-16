package designmodels.creation.singleton;

import lombok.extern.slf4j.Slf4j;

/**
 * 懒汉单例模式有线程安全问题，所以在getInstance方法中加入锁确保只实例一次
 */
@Slf4j
public class LazyGod {
    private static LazyGod god;

    private LazyGod() {

    }

    public static LazyGod getInstance(){


        if(god==null){
            log.info("God还没有实例化。。。");
            synchronized (LazyGod.class) {
                if(god == null){
                    log.info("God第一次被实例化。。。");
                    god = new LazyGod();
                }
            }

        }
        log.info("获取God懒汉单例");
        return god;
    }
}
