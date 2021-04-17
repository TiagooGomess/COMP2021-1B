package analysis.statement;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

public class Expression extends Statement {
    private final Value expression;

    private Expression(SymbolTable table, Method method, Value expression) {
        this.table = table;
        this.method = method;
        this.expression = expression;
    }

    public String getOllir() {
        StringBuilder result = new StringBuilder();
        Value.addValueToBuilder(result, this.expression, this.method);
        result.append(";");
        return result.toString();
    }

    public static Expression fromNode(SymbolTable table, Method currentMethod, JmmNode node) throws JmmException {
        Value expression = Value.fromNode(table, currentMethod, node, null);
        return new Expression(table, currentMethod, expression);
    }
}
