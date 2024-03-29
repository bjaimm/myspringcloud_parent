package designmodels.creation.abstractfactory;

public class BossFactory implements Factory{
    @Override
    public Enemy create(int screenWidth) {
        return new Boss(screenWidth/2,0);
    }
}
