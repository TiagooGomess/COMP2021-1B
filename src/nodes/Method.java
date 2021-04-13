package nodes;

import nodes.expression.Expression;
import nodes.expression.Terminal;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class Method implements Expression {
    private String methodName;
    private boolean isStatic;
    private Type returnType;

    private List<Symbol> parameters;
    private List<Symbol> localVariables;

    public Method(String methodName, Type returnType) {
        this.methodName = methodName;
        this.returnType = returnType;
        this.isStatic = false;
        this.parameters = new ArrayList<>();
        this.localVariables = new ArrayList<>();
    }

    public Method(String methodName, Type returnType, List<Symbol> parameters) {
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameters = parameters;
        this.isStatic = true;
        this.localVariables = new ArrayList<>();
    }

    public Method(String methodName, Type returnType, List<Symbol> parameters, boolean isStatic, List<Symbol> localVariables) {
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameters = parameters;
        this.isStatic = isStatic;
        this.localVariables = localVariables;
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public String getName() {
        return this.methodName;
    }

    @Override
    public Type getReturnType() {
        return this.returnType;
    }

    public List<Symbol> getParameters() {
        return this.parameters;
    }

    public List<Symbol> getLocalVariables() {
        return this.localVariables;
    }

    public Type getVariableType(String variableName) {
        for (Symbol variable : this.localVariables) {
            if (variable.getName().equals(variableName)) {
                return variable.getType();
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    // Adders
    // ----------------------------------------------------------------

    public void addParameter(Symbol parameter) {
        this.parameters.add(parameter);
    }

    public void addLocalVariable(Symbol localVariable) {
        this.localVariables.add(localVariable);
    }

    // ----------------------------------------------------------------
    // Creating from node
    // ----------------------------------------------------------------

    public static Method fromNode(JmmNode node) {
        String methodName = node.get("name");
        Type returnType = Program.stringToType(node.get("type"));
        return new Method(methodName, returnType);
    }

    // ----------------------------------------------------------------
    // Output
    // ----------------------------------------------------------------

    public String toString(String padding) {
        StringBuilder result = new StringBuilder(padding);
        result.append(this.methodName).append(" : ").append(this.returnType.getName());
        if (this.returnType.isArray())
            result.append("[]");

        result.append("\n").append(padding).append("  ").append("Parameters");
        if (this.parameters.size() == 0) {
            result.append("\n").append(padding).append("    none");
        }
        for (Symbol parameter : this.parameters) {
            result.append("\n").append(padding).append("    ");
            result.append(parameter.toString());
        }

        result.append("\n").append(padding).append("  ").append("Variables");
        if (this.localVariables.size() == 0) {
            result.append("\n").append(padding).append("    none");
        }
        for (Symbol variable : this.localVariables) {
            result.append("\n").append(padding).append("    ");
            result.append(variable.toString());
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return this.toString("");
    }
}
