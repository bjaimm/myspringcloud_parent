package designmodels.structure.decoration;

public class FoundationDecorator extends Decorator{
    public FoundationDecorator(Showable showable) {
        super(showable);
    }

    @Override
    public void show() {
        decorator1();
        super.show();
        decorator2();
    }

    public void decorator1(){
        System.out.print("打粉底(");
    }

    public void decorator2(){
        System.out.print(")");
    }
}
