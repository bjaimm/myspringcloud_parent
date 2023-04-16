package designmodels.creation.abstractfactory;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TestAbstractFactory {

    @Test
    public void test()  {
        Factory factory = new RandomFactory();

        for (int i = 0; i < 10;i++) {
            factory.create(100).show();
        }

        factory=new BossFactory();
        factory.create(100).show();
    }
}
