package designmodels.creation.abstractfactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Boss extends Enemy{
    public Boss(int x, int y) {
        super(x, y);
    }

    @Override
    public void show() {
        log.info("Boss出现在坐标({},{})",x,y);
    }
}
