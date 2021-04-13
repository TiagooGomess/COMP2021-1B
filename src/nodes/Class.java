package nodes;

import nodes.expression.Terminal;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.*;

public class Class {
    private String className;
    private String classPath;
    private final Type classType;
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
        this.className = className;
        this.classType = new Type(this.className, false);
        this.superClassName = superClassName;
        this.attributes = attributes;
        this.methods = methods;

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

    public Method getMethod(String methodName) {
        for (Method method : this.methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    public Type getReturnType(String methodName) {
        for (Method method : this.methods) {
            if (method.getName().equals(methodName)) {
                return method.getReturnType();
            }
        }
        return null;
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

    public Type getVariableType(String methodName, String variableName) {
        Type type = null;

        // Search for method and find the local variable
        for (Method method : this.methods) {
            if (method.getName().equals(methodName)) {
                type = method.getVariableType(variableName);
                break;
            }
        }
        if (type != null)
            return type;

        // Search for a field in the class
        for (Terminal attribute : this.attributes)
            if (attribute.getName().equals(variableName))
                return attribute.getType();

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
