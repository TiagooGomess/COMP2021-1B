package analysis.value;

import analysis.method.Method;
import analysis.symbol.Program;
import analysis.symbol.Class;
import analysis.symbol.SymbolTable;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class Terminal extends Value {
    private Symbol symbol;

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

    public String getOllir() {
        if (symbol.getName().equals("this"))
            return "this";
        return symbol.getName() + typeToOllir(symbol.getType());
    }

    // ----------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------

    public void setType(Type type) {
        this.symbol = new Symbol(type, this.getName());
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
        // TODO: verify if type exists instead of creating it
        return new Terminal(Program.stringToType(node.get("type")), node.get("name"));
    }

    public static Value fromVariable(SymbolTable table, Method scopeMethod, JmmNode node, Type expectedType) throws JmmException {
        // The this special word is a variable
        String variableName = node.getKind().equals("This") ? "this" : node.get("name");
        Value variable = table.getVariable(scopeMethod, variableName);
        if (variable == null) {
            Class parentClass = scopeMethod.getParentClass();
            if (parentClass.getSuperName() == null)
                throw JmmException.undeclaredVariable(variableName);
            Terminal terminalVariable = new Terminal(expectedType, variableName);
            parentClass.addAttribute(terminalVariable);
            variable = terminalVariable;
        }
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
