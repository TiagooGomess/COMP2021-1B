import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.List;
import java.util.ArrayList;

public class JmmMethodSymbolTable {
    private Type returnType;
    private List<Symbol> parameters = new ArrayList<>();
    private List<Symbol> localVariables = new ArrayList<>();

    public JmmMethodSymbolTable(Type returnType) {
        this.returnType = returnType;
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

    public void addParameter(Symbol parameter) {
        this.parameters.add(parameter);
    }

    public void addLocalVariable(Symbol variable) {
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
