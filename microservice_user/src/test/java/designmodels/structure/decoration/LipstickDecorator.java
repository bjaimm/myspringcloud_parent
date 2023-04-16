package designmodels.structure.decoration;

public class LipstickDecorator extends Decorator{
    public LipstickDecorator(Showable showable) {
        super(showable);
    }

    @Override
    public void show() {
        decorator1();
        super.show();
        decorator2();
    }

    public void decorator1(){
        System.out.print("涂口红(");
    }

    public void decorator2(){
        System.out.print(")");
    }
}
