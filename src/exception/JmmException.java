package exception;

import analysis.value.Terminal;
import analysis.value.Value;
import analysis.value.function.Access;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class JmmException extends Exception {
    private JmmException(String message) {
        super(message);
    }

    private static String getOutputType(Type type) {
        return type.isArray() ? type.getName() + "[]" : type.getName();
    }

    public static JmmException undeclaredVariable(String variableName) {
        return new JmmException("Variable \"" + variableName + "\" was not declared in the scope");
    }

    public static JmmException invalidNumberOfArguments(String methodName, int expected, int found) {
        return new JmmException("Invalid number of arguments for " + methodName + ", was expecting " + expected + " and found " + found);
    }

    public static JmmException invalidTypeForArgument(String methodName, String parameterName, Type expecting, Type found) {
        return new JmmException("Invalid type for parameter \"" + parameterName + "\" of " + methodName + ", was expecting \"" + getOutputType(expecting) + "\" and found \"" + getOutputType(found) + "\"");
    }

    public static JmmException invalidAssignment(Value variable, Type found) {
        String variableName;
        if (variable instanceof Access)
            variableName = ((Access) variable).getVariableName() + "[]";
        else
            variableName = ((Terminal) variable).getName();
        return new JmmException("Invalid type for assignment of variable \"" + variableName + "\", was expecting \"" + getOutputType(variable.getReturnType()) + "\" and found \"" + getOutputType(found) + "\"");
    }

    public static JmmException invalidCondition(Type found) {
        return new JmmException("Invalid condition expression, was expecting a \"boolean\" and found \"" + getOutputType(found) + "\"");
    }
}
