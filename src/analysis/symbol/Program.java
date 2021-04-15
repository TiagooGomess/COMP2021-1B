package analysis.symbol;

import analysis.method.Method;
import analysis.value.Terminal;
import analysis.value.Value;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Program {
    public static final Type INT_TYPE = new Type("int", false);
    public static final Type BOOL_TYPE = new Type("boolean", false);
    public static final Type INT_ARRAY_TYPE = new Type("int", true);

    // Global methods
    private final List<Method> methods = new ArrayList<>();

    // Classes
    private final List<Class> externalClasses = new ArrayList<>();
    private Class mainClass;

    public Program() {
        this.addOperatorMethods();
        this.addIntArray();
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public List<Class> getExternalClasses() {
        return externalClasses;
    }

    public Class getMainClass() {
        return mainClass;
    }

    public Class getClass(String className) {
        if (this.mainClass.getName().equals(className))
            return this.mainClass;
        for (Class externalClass : this.externalClasses)
            if (externalClass.getName().equals(className))
                return externalClass;
        return null;
    }

    public List<Method> getMethod(String className, String methodName) {
        Class requiredClass = this.getClass(className);
        if (requiredClass == null)
            return null;
        return requiredClass.getMethod(methodName);
    }

    public List<Method> getMethod(String methodName) {
        // Valid method declared in the file
        List<Method> methods = this.mainClass.getMethod(methodName);
        if (!methods.isEmpty())
            return methods;

        // Search in the operators methods
        for (Method operator : this.methods)
            if (operator.getName().equals(methodName))
                return Collections.singletonList(operator);

        return null;
    }

    public Value getVariable(Method scopeMethod, String variableName) {
        // Searches for the variable in the class scope
        Value variable = this.mainClass.getVariable(scopeMethod, variableName);
        if (variable != null)
            return variable;

        // Searches for class references in imports
        for (Class externalClass : this.externalClasses) {
            if (externalClass.getName().equals(variableName))
                return externalClass;
        }

        // Variable could not be found
        return null;
    }

    // ----------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------

    public void setMainClass(Class mainClass) {
        this.mainClass = mainClass;
    }

    // ----------------------------------------------------------------
    // Adders
    // ----------------------------------------------------------------

    public void addExternalClass(Class externalClass) {
        this.externalClasses.add(externalClass);
    }

    // ----------------------------------------------------------------
    // Functions for adding default operators and types
    // ----------------------------------------------------------------

    private void addOperatorMethods() {
        this.addOperatorMethod("Addition", INT_TYPE,
                Arrays.asList(new Terminal(INT_TYPE, "left addend"), new Terminal(INT_TYPE, "right addend")));

        this.addOperatorMethod("Subtraction", INT_TYPE,
                Arrays.asList(new Terminal(INT_TYPE, "subtrahend"), new Terminal(INT_TYPE, "minuend")));

        this.addOperatorMethod("Multiplication", INT_TYPE,
                Arrays.asList(new Terminal(INT_TYPE, "multiplicand"), new Terminal(INT_TYPE, "multiplier")));

        this.addOperatorMethod("Division", INT_TYPE,
                Arrays.asList(new Terminal(INT_TYPE, "dividend"), new Terminal(INT_TYPE, "divisor")));

        this.addOperatorMethod("Comparison", BOOL_TYPE,
                Arrays.asList(new Terminal(INT_TYPE, "left expression"), new Terminal(INT_TYPE, "right expression")));

        this.addOperatorMethod("Conjunction", BOOL_TYPE,
                Arrays.asList(new Terminal(BOOL_TYPE, "left expression"), new Terminal(BOOL_TYPE, "right expression")));

        this.addOperatorMethod("Negation", BOOL_TYPE,
                Collections.singletonList(new Terminal(BOOL_TYPE, "expression")));

        this.addOperatorMethod("Access", INT_TYPE,
                Arrays.asList(new Terminal(INT_ARRAY_TYPE, "container"), new Terminal(INT_TYPE, "position")));
    }

    private void addOperatorMethod(String operatorName, Type returnType, List<Terminal> argumentTypes) {
        this.methods.add(new Method("%" + operatorName, returnType, argumentTypes));
    }

    private void addIntArray() {
        Class intArray = new Class("int[]");
        Method length = new Method("length", INT_TYPE);
        intArray.addMethod(length);
        this.externalClasses.add(intArray);
    }

    // ----------------------------------------------------------------
    // Static function to create new types from strings
    // ----------------------------------------------------------------

    public static Type stringToType(String typeName) {
        int size = typeName.length();
        boolean isArray = false;
        if (typeName.startsWith("[]", size - 2)) {
            isArray = true;
            typeName = typeName.substring(0, size - 2);
        }
        return new Type(typeName, isArray);
    }

    // ----------------------------------------------------------------
    // Output
    // ----------------------------------------------------------------

    @Override
    public String toString() {
        if (this.mainClass == null)
            return null;
        return this.mainClass.toString();
    }
}
