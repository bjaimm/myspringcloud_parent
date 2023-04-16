package designmodels.structure.decoration;

import org.junit.Test;

public class TestDecoration {

    public static void main(String[] args) {
        Girl girl = new Girl();


        LipstickDecorator decorator = new LipstickDecorator(girl);
        FoundationDecorator decorator2 = new FoundationDecorator(decorator);
        decorator2.show();

        System.out.println();
        System.out.println("=======================");
        Showable showable = new LipstickDecorator(new FoundationDecorator(girl));
        showable.show();
    }
}
