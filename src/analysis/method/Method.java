package analysis.method;

import analysis.symbol.Class;
import analysis.symbol.Program;
import analysis.symbol.SymbolTable;
import analysis.value.Terminal;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class Method extends Value {
    private Class parentClass;
    private MethodSignature signature;
    private Type returnType;
    private List<Terminal> localVariables;

    public Method(Class parentClass, String methodName, Type returnType) {
        this.parentClass = parentClass;
        this.signature = new MethodSignature(methodName);
        this.returnType = returnType;
        this.localVariables = new ArrayList<>();
    }

    public Method(Class parentClass, String methodName, Type returnType, List<Terminal> parameters) {
        this.parentClass = parentClass;
        this.signature = new MethodSignature(methodName, parameters);
        this.returnType = returnType;
        this.localVariables = new ArrayList<>();
    }

    public Method(Class parentClass, String methodName, Type returnType, List<Terminal> parameters, boolean isStatic, List<Terminal> localVariables) {
        this.parentClass = parentClass;
        this.signature = new MethodSignature(methodName, parameters, isStatic);
        this.returnType = returnType;
        this.localVariables = localVariables;
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public Class getParentClass() {
        return this.parentClass;
    }

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

    public int getArgumentNumber(Terminal argument) {
        // Returns 0 if not found
        return this.getParameters().indexOf(argument) + 1;
    }

    public boolean isStatic() {
        return this.signature.isStatic();
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

        // Search in class fields
        for (Terminal variable : this.parentClass.getAttributes())
            if (variable.getName().equals(variableName))
                return variable;

        return null;
    }

    public MethodSignature getSignature() {
        return this.signature;
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
        parameter.setIsParameter();
        this.getParameters().add(parameter);
    }

    public void addLocalVariable(Terminal localVariable) {
        this.localVariables.add(localVariable);
    }

    // ----------------------------------------------------------------
    // Creating from node
    // ----------------------------------------------------------------

    public static Method fromDeclaration(SymbolTable table, Class parentClass, JmmNode node) throws JmmException {
        String methodName = node.get("name");
        Type returnType = table.getType(node.get("type"));
        return new Method(parentClass, methodName, returnType);
    }

    // ----------------------------------------------------------------
    // Output
    // ----------------------------------------------------------------

    public String toString(String padding) {
        StringBuilder result = new StringBuilder(padding);
        String type = "?";
        if (this.returnType != null) {
            type = this.returnType.getName();
            if (this.returnType.isArray())
                type += "[]";
        }
        result.append(this.getName()).append(" : ").append(type);

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

    @Override
    public String getOllir() {
        StringBuilder builder = new StringBuilder();
        if (this.getName().equals("%Construction")) {
            builder.append(".construct ").append(this.getReturnType().getName()).append("().V");
        } else {
            builder.append(".method public ");
            if (this.isStatic())
                builder.append("static ");
            builder.append(this.signature.getMethodName());
            builder.append("(");

            // Arguments
            List<String> argumentOllir = new ArrayList<>();
            for (Terminal terminal : this.signature.getParameters())
                argumentOllir.add(terminal.getOllir());
            builder.append(String.join(", ", argumentOllir));

            builder.append(")");
            builder.append(Value.typeToOllir(this.returnType));
        }

        return builder.toString();
    }
}
