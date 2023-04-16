package testcases.FunctionInterfaceExample;

public class FunctionInterfaceUtils {
    public static ConsumerFunctionThrowException isTrue(boolean param){
        return exceptionMessage -> {
            if(param){
                throw new RuntimeException(exceptionMessage);
            }
        };
    }
}
