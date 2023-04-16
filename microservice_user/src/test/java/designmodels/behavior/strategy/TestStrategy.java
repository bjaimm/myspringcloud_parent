package designmodels.behavior.strategy;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TestStrategy {

    @Test
    public void test()  {
        Calclulater calclulater = new Calclulater();

        log.info("两数相减:{}-{}={}",3,4,calclulater.getCalclulateResult(3,4));

        calclulater.setStrategy(StrategyConstants.SUM_STRATEGY);

        log.info("两数相减:{}+{}={}",3,4,calclulater.getCalclulateResult(3,4));

    }
}
