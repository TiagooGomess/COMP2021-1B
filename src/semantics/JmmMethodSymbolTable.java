package semantics;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.List;
import java.util.ArrayList;

public class JmmMethodSymbolTable {
    private final String className;
    private final String name;
    private final Type returnType;
    private final List<Symbol> parameters;
    private final List<Symbol> localVariables = new ArrayList<>();
    private final boolean isStatic;

    public JmmMethodSymbolTable(String name, String className, Type returnType) {
        this.name = name;
        this.className = className;
        this.returnType = returnType;
        this.isStatic = this.name.equals("main");
        this.parameters = new ArrayList<>();
    }

    public JmmMethodSymbolTable(String name, String className, Type returnType, boolean isStatic, List<Symbol> parameters) {
        this.name = name;
        this.className = className;
        this.returnType = returnType;
        this.isStatic = isStatic;
        this.parameters = parameters;
    }

    public String getClassName() {
        return this.className;
    }

    public String getName() {
        return this.name;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

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
        for (Symbol variable : localVariables) {
            if (variable.getName().equals(variableName)) {
                return variable.getType();
            }
        }
        for (Symbol parameter : parameters) {
            if (parameter.getName().equals(variableName)) {
                return parameter.getType();
            }
        }
        return null;
    }

    public void addParameter(Symbol parameter) throws Exception {
        for (Symbol symbol : this.parameters)
            if (symbol.getName().equals(parameter.getName()))
                throw new Exception("The parameter with name \"" + parameter.getName() + "\" is already defined in the method scope.");

        this.parameters.add(parameter);
    }

    public void addLocalVariable(Symbol variable) throws Exception {
        for (Symbol symbol : this.parameters)
            if (symbol.getName().equals(variable.getName()))
                throw new Exception("The local variable with name \"" + variable.getName() + "\" is already defined in the method scope");
        for (Symbol symbol : this.localVariables)
            if (symbol.getName().equals(variable.getName()))
                throw new Exception("The local variable with name \"" + variable.getName() + "\" is already defined in the method scope");

        this.localVariables.add(variable);
    }

    public String toString(String spaces) {
        StringBuilder b = new StringBuilder();

        b.append(spaces);
        b.append("Type:\n");
        b.append(spaces);
        b.append("  ");
        b.append(this.returnType.toString());
        b.append("\n");

        if (this.parameters.size() > 0) {
            b.append(spaces);
            b.append("Parameters:\n");
            for (Symbol parameter : this.parameters) {
                b.append(spaces);
                b.append("  ");
                b.append(parameter.toString());
                b.append("\n");
            }
        }

        if (this.localVariables.size() > 0) {
            b.append(spaces);
            b.append("Local Variables:\n");
            for (Symbol variable : this.localVariables) {
                b.append(spaces);
                b.append("  ");
                b.append(variable.toString());
                b.append("\n");
            }
        }

        return b.toString();
    }

    public String toString() {
        return toString("");
    }
}
