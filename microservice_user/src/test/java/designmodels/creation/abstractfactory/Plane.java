package designmodels.creation.abstractfactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Plane extends Enemy{
    public Plane(int x, int y) {
        super(x, y);
    }

    @Override
    public void show() {
        log.info("飞机出现在坐标({},{})",x,y);
    }
}
