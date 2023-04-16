package designmodels.creation.abstractfactory;

import java.util.Random;

public class RandomFactory implements Factory {

    private Random random = new Random();

    @Override
    public Enemy create(int screenWidth) {

        Enemy enemy =null;

        if(random.nextBoolean()){
            enemy = new Plane(random.nextInt(screenWidth),0);
        }else {
            enemy = new Tank(random.nextInt(screenWidth), 0);
        }
        return enemy;
    }
}
