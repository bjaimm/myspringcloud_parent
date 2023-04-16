package designmodels.creation.abstractfactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Tank extends Enemy{
    public Tank(int x, int y) {
        super(x, y);
    }

    @Override
    public void show() {
        log.info("坦克出现在坐标({},{})",x,y);
    }
}
