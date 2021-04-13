package nodes;

import nodes.expression.Terminal;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
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
    // TODO int array constructor

    // Classes
    private final List<Class> externalClasses = new ArrayList<>();
    private Class mainClass;

    public Program() {
        this.addOperatorMethods();
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
    // Functions for adding the operators as methods
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

        this.addOperatorMethod("Not", BOOL_TYPE,
                Collections.singletonList(new Terminal(BOOL_TYPE, "expression")));

        this.addOperatorMethod("Access", INT_TYPE,
                Arrays.asList(new Terminal(INT_ARRAY_TYPE, "array"), new Terminal(INT_TYPE, "position")));
    }

    private void addOperatorMethod(String operatorName, Type returnType, List<Terminal> argumentTypes) {
        this.methods.add(new Method("%" + operatorName, returnType, argumentTypes));
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
