package designmodels.creation.singleton;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EagerGod {
    private static final EagerGod god = new EagerGod();

    private EagerGod() {

    }

    public static EagerGod getInstance(){
        log.info("获取God饿汉单例");
        return god;
    }
}
