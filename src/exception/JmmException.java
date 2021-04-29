package exception;

import analysis.symbol.Class;
import analysis.value.Terminal;
import analysis.value.Value;
import analysis.value.function.Access;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.List;
import java.util.ArrayList;

public class JmmException extends Exception {
    private int line = 0;
    private int column = 0;

    private JmmException(String message) {
        super(message);
    }

    private JmmException(String message, String line, String col) {
        super(message);
        this.line = Integer.parseInt(line);
        this.column = Integer.parseInt(col);
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    private static String getOutputType(Type type) {
        if (type == null)
            return "unknown type";
        return type.isArray() ? type.getName() + "[]" : type.getName();
    }

    public static JmmException undeclaredVariable(JmmNode errorNode, String variableName) {
        return new JmmException("Variable \"" + variableName + "\" was not declared in the scope", errorNode.get("line"), errorNode.get("col"));
    }

    public static JmmException attributeAlreadyDefined(String variableName, String className) {
        return new JmmException("Field \"" + variableName + "\" is already defined in class \"" + className + "\"");
    }

    public static JmmException methodAlreadyDefined(String methodName, String className, List<Terminal> parameters) {
        List<String> types = new ArrayList<>();
        for (Value value : parameters)
            types.add("\"" + getOutputType(value.getReturnType()) + "\"");
        String parameterString = types.size() > 0 ? "(" + String.join(", ", types) + ")" : "no";
        return new JmmException("Method \"" + methodName + "\" with " + parameterString + " parameters is already defined in class \"" + className + "\"");
    }

    public static JmmException variableAlreadyDefined(String variableName) {
        return new JmmException("Variable \"" + variableName + "\" is already defined in the scope");
    }

    public static JmmException uninitializedVariable(String variableName) {
        return new JmmException("Variable \"" + variableName + "\" was not initialized");
    }

    public static JmmException invalidNumberOfArguments(String methodName, int found) {
        return new JmmException("Invalid number of arguments for " + methodName + ", no implementation with " + found + " parameters found");
    }

    public static JmmException invalidTypeForArguments(String methodName, List<Value> arguments) {
        List<String> types = new ArrayList<>();
        for (Value value : arguments)
            types.add("\"" + getOutputType(value.getReturnType()) + "\"");
        String parameterString = types.size() > 0 ? "(" + String.join(", ", types) + ")" : "no";
        return new JmmException("Invalid call for " + methodName + ", no implementation with " + parameterString + " parameters found");
    }

    public static JmmException invalidAssignmentVariable() {
        return new JmmException("Invalid left operand for assignment, was expecting a variable found expression");
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

    public static JmmException invalidType(String typeName) {
        return new JmmException("Invalid type \"" + typeName + "\"");
    }

    public static JmmException invalidCaller(JmmNode errorNode, String methodName, Value caller) {
        String explanationString = (caller instanceof Class) ? "was expecting instance of class and found class reference" : "was expecting class reference and found instance of class";
        return new JmmException("Invalid caller of method \"" + methodName + "\" of class \"" + caller.getReturnType().getName() + "\", " + explanationString, errorNode.get("line"), errorNode.get("col"));
    }
}
