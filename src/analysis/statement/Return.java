package analysis.statement;

import analysis.symbol.SymbolTable;
import analysis.value.Terminal;
import analysis.value.Value;
import analysis.method.Method;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

public class Return extends Statement {
    private final Value returnValue;

    private Return(SymbolTable table, Method currentMethod, Value returnValue) {
        this.table = table;
        this.method = currentMethod;
        this.returnValue = returnValue;
    }

    public String getOllir() {
        StringBuilder builder = new StringBuilder();

        builder.append("ret").append(Terminal.typeToOllir(this.returnValue.getReturnType())).append(" ");
        Value.addValueToBuilder(builder, this.returnValue, this.method);
        builder.append(";");

        return builder.toString();
    }

    public static Return fromNode(SymbolTable table, Method currentMethod, JmmNode node) throws JmmException {
        JmmNode expressionNode = node.getChildren().get(0);
        Value returnExpression = Value.fromNode(table, currentMethod, expressionNode, currentMethod.getReturnType());
        if (!currentMethod.getReturnType().equals(returnExpression.getReturnType()))
            throw JmmException.invalidReturn(node, currentMethod.getName(), currentMethod.getReturnType(), returnExpression.getReturnType());
        return new Return(table, currentMethod, returnExpression);
    }
}
