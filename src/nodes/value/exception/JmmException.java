package nodes.value.exception;

import pt.up.fe.comp.jmm.analysis.table.Type;

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

    public static JmmException invalidTypeForArgument(String methodName, String parameterName, Type expecting, Type found) {
        return new JmmException("Invalid type for parameter \"" + parameterName + "\" of method \"" + methodName + "\", was expecting \"" + expecting + "\", found \"" + found + "\"");
    }
}
