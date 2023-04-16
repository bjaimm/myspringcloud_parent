package designmodels.behavior.strategy;

public class Calclulater {
    private Strategy strategy;

    public Calclulater()
    {
        this.strategy = new DeduceStrategy();
    }

    public void setStrategy(Strategy strategy){
        this.strategy = strategy;
    }

    public void setStrategy(String strategyName){
        try {
            this.strategy= (Strategy) Class.forName(strategyName).newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public int getCalclulateResult(int a ,int b){
        return strategy.calculate(a,b);
    }
}
