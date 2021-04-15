package exception;

import analysis.value.Terminal;
import analysis.value.Value;
import analysis.value.function.Access;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.List;

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

    public static JmmException invalidNumberOfArguments(String methodName, int found) {
        return new JmmException("Invalid number of arguments for " + methodName + ", no implementation with " + found + " parameters found");
    }

    public static JmmException invalidTypeForArguments(String methodName, List<JmmNode> arguments) {
        return new JmmException("Invalid call for method " + methodName + ", not implementation with (TODO) parameters found");
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
        return new JmmException("Invalid condition expression, was expecting \"boolean\" and found \"" + getOutputType(found) + "\"");
    }

    public static JmmException invalidReturn(String methodName, Type expected, Type found) {
        return new JmmException("Invalid return expression for method \"" + methodName + "\", was expecting \"" + getOutputType(expected) + "\" and found \"" + getOutputType(found) + "\"");
    }

    public static JmmException invalidMethod(String methodName) {
        return new JmmException("Method \"" + methodName + "\" could not be found");
    }
}
