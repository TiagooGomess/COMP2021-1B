package analysis.symbol;

import analysis.method.Method;
import analysis.value.Terminal;
import analysis.value.Value;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.*;

public class Class extends Value {
    private String className;
    private String classPath;
    private Type classType;
    private final String superClassName;
    private final List<Terminal> attributes;
    private final List<Method> methods;

    public Class(String className) {
        this(className, null, new ArrayList<>(), new ArrayList<>());
    }

    public Class(String className, String superClassName) {
        this(className, superClassName, new ArrayList<>(), new ArrayList<>());
    }

    public Class(String className, String superClassName, List<Terminal> attributes, List<Method> methods) {
        this.processClassName(className);
        this.superClassName = superClassName;
        this.attributes = attributes;
        this.methods = methods;

        this.classType = new Type(this.className, false);

        // Constructor method
        if (!this.className.equals("int[]"))
            this.methods.add(new Method("%Construction", this.classType, Collections.emptyList()));
        else
            this.methods.add(new Method("%Construction", this.classType, Collections.singletonList(new Terminal(Program.INT_TYPE, "array size"))));
        // This expression
        this.attributes.add(new Terminal(this.classType, "this"));
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public String getName() {
        return this.className;
    }

    public String getImportName() {
        return this.classPath + "." + this.className;
    }

    public String getSuperName() {
        return this.superClassName;
    }

    public List<Terminal> getAttributes() {
        return attributes;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public Type getReturnType(String methodName) {
        for (Method method : this.methods) {
            if (method.getName().equals(methodName)) {
                return method.getReturnType();
            }
        }
        return null;
    }

    public Type getReturnType() {
        return this.classType;
    }

    public List<Terminal> getParameters(String methodName) {
        for (Method method : this.methods) {
            if (method.getName().equals(methodName)) {
                return method.getParameters();
            }
        }
        return null;
    }

    public List<Terminal> getLocalVariables(String methodName) {
        for (Method method : this.methods) {
            if (method.getName().equals(methodName)) {
                return method.getLocalVariables();
            }
        }
        return null;
    }

    public Method getMethod(String methodName) {
        for (Method method : this.methods)
            if (method.getName().equals(methodName))
                return method;
        return null;
    }

    public Value getVariable(Method scopeMethod, String variableName) {
        // First verify if it is a local variable
        Terminal variable = scopeMethod.getVariable(variableName);
        if (variable != null)
            return variable;

        // If it is not a local variable search in class fields
        for (Terminal terminal : this.attributes)
            if (terminal.getName().equals(variableName))
                return terminal;

        // Maybe it is a reference to the class itself
        if (this.className.equals(variableName))
            return this;

        return null;
    }

    // ----------------------------------------------------------------
    // Adders
    // ----------------------------------------------------------------

    public void addAttribute(Terminal attribute) {
        this.attributes.add(attribute);
    }

    public void addMethod(Method method) {
        this.methods.add(method);
    }

    // ----------------------------------------------------------------
    // Function for the processing of class path if it is an import
    // ----------------------------------------------------------------

    private void processClassName(String className) {
        List<String> steps = Arrays.asList(className.split("\\."));
        int size = steps.size();
        this.className = steps.get(size - 1);
        this.classPath = String.join(".", steps.subList(0, size - 1));
    }

    // ----------------------------------------------------------------
    // Creating from node
    // ----------------------------------------------------------------

    public static Class fromNode(JmmNode node) {
        String className = node.get("name");
        String superName = node.getAttributes().contains("extends") ? node.get("extends") : null;
        return new Class(className, superName);
    }

    // ----------------------------------------------------------------
    // Output
    // ----------------------------------------------------------------

    public String toString(String padding) {
        StringBuilder result = new StringBuilder();

        result.append(padding).append("Class ").append(this.className);
        if (this.superClassName != null)
            result.append(" : ").append(this.superClassName);

        result.append("\n").append(padding).append("Attributes");
        for (Terminal attribute : this.getAttributes()) {
            result.append("\n").append(padding).append("  ");
            result.append(attribute.toString());
        }

        result.append("\n").append(padding).append("Methods");
        for (Method method : this.getMethods()) {
            result.append("\n");
            result.append(method.toString(padding + "  "));
        }

        return result.toString();
    }

    @Override
    public String toString() {
        return this.toString("");
    }
}
