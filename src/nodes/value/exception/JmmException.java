package nodes.value.exception;

public class JmmException extends Exception {
    private JmmException(String message) {
        super(message);
    }

    public static JmmException undeclaredVariable(String variableName) {
        return new JmmException("Variable \"" + variableName + "\" was not declared in the scope");
    }

    public static JmmException invalidNumberOfArguments(String methodName, int expected, int found) {
        return new JmmException("Invalid number of arguments for " + methodName + ", was expecting " + expected + ", found " + found);
    }
}
