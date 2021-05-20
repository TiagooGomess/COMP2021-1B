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
    private boolean isInitiated = false;
    private boolean isLiteral = false;
    private boolean isParameter = false;

    public Terminal(Type type, String name) {
        this.symbol = new Symbol(type, name);
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public boolean isLiteral() {
        return this.isLiteral;
    }

    public boolean isParameter() {
        return this.isParameter;
    }

    public boolean isInitiated() {
        return this.isInitiated;
    }

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

    public boolean isReservedWord(String word) {
        return switch (word) {
            case "ret", "putfield", "getfield", "array", "field", "invokespecial", "invokestatic", "construct", "method", "public", "static", "main", "String" -> true;
            default -> word.startsWith("aux");
        };
    }

    public String getOllirName() { // Deal with the case when a variable name is a ollir reserved word
        return (this.isReservedWord(getName()) ? "__" : "") + symbol.getName().replace("$", "__");
    }

    public String getOllir() {
        if (symbol.getName().equals("this"))
            return "this" + typeToOllir(symbol.getType());
        return getOllirName() + typeToOllir(symbol.getType());
    }

    // ----------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------

    public void setIsLiteral() {
        this.isLiteral = true;
    }

    public void setIsParameter() {
        this.isParameter = true;
    }

    public void setInitiated() {
        this.isInitiated = true;
    }

    public void setType(Type type) {
        this.symbol = new Symbol(type, this.getName());
    }

    // ----------------------------------------------------------------
    // Creating from node
    // ----------------------------------------------------------------

    // Creating of a terminal that represents a literal in the code
    // valid literals are integers and booleans
    public static Terminal fromLiteral(SymbolTable table, JmmNode node) throws JmmException {
        Type type = table.getType(node.get("type"));
        String value = node.get("value");
        if (value.equals("true"))
            value = "1";
        else if (value.equals("false"))
            value = "0";
        return new Terminal(type, value);
    }

    public static Terminal fromDeclaration(SymbolTable table, JmmNode node) throws JmmException {
        Type type = table.getType(node.get("type"));
        return new Terminal(type, node.get("name"));
    }

    public static Value fromVariable(SymbolTable table, Method scopeMethod, JmmNode node, Type expectedType) throws JmmException {
        // The this special word is a variable
        String variableName = node.getKind().equals("This") ? "this" : node.get("name");
        Value variable = table.getVariable(scopeMethod, variableName);
        if (variable == null) {
            Class parentClass = scopeMethod.getParentClass();
            if (parentClass.getSuperName() == null)
                throw JmmException.undeclaredVariable(node, variableName);
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
