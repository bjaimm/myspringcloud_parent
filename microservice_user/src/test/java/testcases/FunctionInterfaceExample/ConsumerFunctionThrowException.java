package testcases.FunctionInterfaceExample;

@FunctionalInterface
public interface ConsumerFunctionThrowException {
    void throwException(String exceptionMessage);
}
