package analysis.method;

import analysis.symbol.Program;
import analysis.value.Terminal;
import analysis.value.Value;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class Method extends Value {
    private MethodSignature signature;
    private boolean isStatic;
    private Type returnType;
    private List<Terminal> localVariables;

    public Method(String methodName, Type returnType) {
        this.signature = new MethodSignature(methodName);
        this.returnType = returnType;
        this.isStatic = false;
        this.localVariables = new ArrayList<>();
    }

    public Method(String methodName, Type returnType, List<Terminal> parameters) {
        this.signature = new MethodSignature(methodName, parameters);
        this.returnType = returnType;
        this.isStatic = true;
        this.localVariables = new ArrayList<>();
    }

    public Method(String methodName, Type returnType, List<Terminal> parameters, boolean isStatic, List<Terminal> localVariables) {
        this.signature = new MethodSignature(methodName, parameters);
        this.returnType = returnType;
        this.isStatic = isStatic;
        this.localVariables = localVariables;
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public String getName() {
        return this.signature.getMethodName();
    }

    @Override
    public Type getReturnType() {
        return this.returnType;
    }

    public List<Terminal> getParameters() {
        return this.signature.getParameters();
    }

    public List<Terminal> getLocalVariables() {
        return this.localVariables;
    }

    public Terminal getVariable(String variableName) {
        // Search in local variables
        for (Terminal variable : this.localVariables)
            if (variable.getName().equals(variableName))
                return variable;

        // Search in parameters
        for (Terminal variable : this.getParameters())
            if (variable.getName().equals(variableName))
                return variable;

        return null;
    }

    // ----------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------

    public void setReturnType(Type type) {
        this.returnType = type;
    }

    // ----------------------------------------------------------------
    // Adders
    // ----------------------------------------------------------------

    public void addParameter(Terminal parameter) {
        this.getParameters().add(parameter);
    }

    public void addLocalVariable(Terminal localVariable) {
        this.localVariables.add(localVariable);
    }

    // ----------------------------------------------------------------
    // Creating from node
    // ----------------------------------------------------------------

    public static Method fromDeclaration(JmmNode node) {
        String methodName = node.get("name");
        Type returnType = Program.stringToType(node.get("type"));
        return new Method(methodName, returnType);
    }

    // ----------------------------------------------------------------
    // Output
    // ----------------------------------------------------------------

    public String toString(String padding) {
        StringBuilder result = new StringBuilder(padding);
        result.append(this.getName()).append(" : ").append(this.returnType.getName());
        if (this.returnType.isArray())
            result.append("[]");

        result.append("\n").append(padding).append("  ").append("Parameters");
        if (this.getParameters().size() == 0) {
            result.append("\n").append(padding).append("    none");
        }
        for (Terminal parameter : this.getParameters()) {
            result.append("\n").append(padding).append("    ");
            result.append(parameter.toString());
        }

        result.append("\n").append(padding).append("  ").append("Variables");
        if (this.localVariables.size() == 0) {
            result.append("\n").append(padding).append("    none");
        }
        for (Terminal variable : this.localVariables) {
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
