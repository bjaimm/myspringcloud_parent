package designmodels.creation.singleton;

import designmodels.behavior.strategy.Calclulater;
import designmodels.behavior.strategy.StrategyConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TestSingleton {

    @Test
    public void test()  {
        EagerGod eagerGod1 = EagerGod.getInstance();
        EagerGod eagerGod2 = EagerGod.getInstance();

        LazyGod eagerGod3 = LazyGod.getInstance();
        LazyGod eagerGod4 = LazyGod.getInstance();
    }
}
