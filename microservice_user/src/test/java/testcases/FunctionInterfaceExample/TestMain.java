package testcases.FunctionInterfaceExample;

public class TestMain {
    public static void main(String[] args) {
        FunctionInterfaceUtils.isTrue(false).throwException("发生异常1。。。");

        FunctionInterfaceUtils.isTrue(true).throwException("发生异常2。。。");
    }
}
