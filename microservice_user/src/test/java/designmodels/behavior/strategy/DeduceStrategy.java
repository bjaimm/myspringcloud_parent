package designmodels.behavior.strategy;

public class DeduceStrategy implements Strategy{
    @Override
    public int calculate(int a, int b) {
        return a-b;
    }
}
