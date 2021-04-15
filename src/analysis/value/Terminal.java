package analysis.value;

import analysis.method.Method;
import analysis.symbol.Program;
import analysis.symbol.SymbolTable;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class Terminal extends Value {
    private final Symbol symbol;

    public Terminal(Type type, String name) {
        this.symbol = new Symbol(type, name);
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public Symbol getSymbol() {
        return symbol;
    }

    public Type getType() {
        return this.symbol.getType();
    }

    public String getName() {
        return this.symbol.getName();
    }

    @Override
    public Type getReturnType() {
        return getType();
    }

    // ----------------------------------------------------------------
    // Creating from node
    // ----------------------------------------------------------------

    // Creating of a terminal that represents a literal in the code
    // valid literals are integers and booleans
    public static Terminal fromLiteral(JmmNode node) {
        return new Terminal(Program.stringToType(node.get("type")), node.get("value"));
    }

    public static Terminal fromDeclaration(JmmNode node) {
        return new Terminal(Program.stringToType(node.get("type")), node.get("name"));
    }

    public static Value fromVariable(SymbolTable table, Method scopeMethod, JmmNode node) throws JmmException {
        // The this special word is a variable
        String variableName = node.getKind().equals("This") ? "this" : node.get("name");
        Value variable = table.getVariable(scopeMethod, variableName);
        if (variable == null)
            throw JmmException.undeclaredVariable(variableName);
        return variable;
    }

    // ----------------------------------------------------------------
    // Output
    // ----------------------------------------------------------------

    public String toString(String padding) {
        String result = padding + this.getName() + " : " + this.getReturnType().getName();
        if (this.getReturnType().isArray())
            result += "[]";
        return result;
    }

    @Override
    public String toString() {
        return this.toString("");
    }
}
