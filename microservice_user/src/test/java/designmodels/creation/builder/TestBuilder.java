package designmodels.creation.builder;

import designmodels.creation.singleton.EagerGod;
import designmodels.creation.singleton.LazyGod;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.jta.bitronix.PoolingConnectionFactoryBean;

@Slf4j
public class TestBuilder {

    @Test
    public void test()  {
        Director director = new Director(new HouseBuilder());

        log.info("建造普通房屋完工:{}",director.direct().toString());

        director.setBuilder(new ApartmentBuilder());

        log.info("建造别墅完工:{}",director.direct().toString());
    }
}
