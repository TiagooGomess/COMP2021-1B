package analysis.symbol;

import analysis.method.Method;
import analysis.method.MethodSignature;
import analysis.value.Terminal;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.*;

public class Class extends Value {
    private String className;
    private String classPath;
    private Type classType;
    private final String superClassName;
    private final List<Terminal> attributes;

    private final Map<MethodSignature, Method> methods;
    // private final List<Method> methods;

    public Class(String className) {
        this(className, null, new ArrayList<>(), new HashMap<>());
    }

    public Class(String className, String superClassName) {
        this(className, superClassName, new ArrayList<>(), new HashMap<>());
    }

    public Class(String className, String superClassName, List<Terminal> attributes, Map<MethodSignature, Method> methods) {
        this.processClassName(className);
        this.superClassName = superClassName;
        this.attributes = attributes;
        this.methods = methods;

        // Constructor method
        Method constructor;
        if (this.className.endsWith("[]")) {
            this.classType = new Type(this.className.substring(0, this.className.length() - 2), true);
            constructor = new Method(this, "%Construction", this.classType, Collections.singletonList(new Terminal(Program.INT_TYPE, "array size")));
        } else {
            this.classType = new Type(this.className, false);
            constructor = new Method(this, "%Construction", this.classType, Collections.emptyList());
        }
        this.methods.put(constructor.getSignature(), constructor);

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
        return new ArrayList<>(this.methods.values());
    }

    public Type getReturnType(String methodName) {
        for (Method method : this.getMethods()) {
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
        for (Method method : this.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method.getParameters();
            }
        }
        return null;
    }

    public List<Terminal> getLocalVariables(String methodName) {
        for (Method method : this.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method.getLocalVariables();
            }
        }
        return null;
    }

    public List<Method> getMethod(String methodName) {
        List<Method> result = new ArrayList<>();
        for (Method method : this.getMethods())
            if (method.getName().equals(methodName))
                result.add(method);
        return result;
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

    public boolean isField(Terminal argument) {
        return this.getAttributes().contains(argument);
    }

    // ----------------------------------------------------------------
    // Adders
    // ----------------------------------------------------------------

    public void addAttribute(Terminal attribute) {
        this.attributes.add(attribute);
    }

    public void addMethod(Method method) {
        this.methods.put(method.getSignature(), method);
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

    public static Class fromNode(SymbolTable table, JmmNode node) throws JmmException {
        String className = node.get("name");
        String superName = node.getAttributes().contains("extends") ? node.get("extends") : null;
        if (superName != null) {
            Type superType = table.getType(superName);
        }
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

    @Override
    public String getOllir() {
        return "";
    }
}
