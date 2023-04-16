package designmodels.behavior.strategy;

public class SumStrategy implements Strategy{
    @Override
    public int calculate(int a, int b) {
        return a+b;
    }
}
