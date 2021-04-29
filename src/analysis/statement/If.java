package analysis.statement;

import analysis.method.Method;
import analysis.symbol.Program;
import analysis.symbol.SymbolTable;
import analysis.value.Terminal;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

public class If extends Statement {
    private static int if_count = 1;
    private Value condition;
    private Statement thenStatement;
    private Statement elseStatement;

    private If(SymbolTable table, Method method, Value condition, Statement thenStatement, Statement elseStatement) {
        this.table = table;
        this.method = method;
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    public String getOllir() {
        String thenLabel = "then" + if_count;
        String endifLabel = "endif" + if_count++;

        StringBuilder builder = new StringBuilder();
        builder.append("if (");
        Terminal t = Value.addValueToBuilder(builder, this.condition, this.method);
        if (t != null || this.condition instanceof Terminal)
            builder.append(" &&.bool true.bool");
        builder.append(") goto ").append(thenLabel).append(";");
        builder.append(("\n" + this.elseStatement.getOllir()).replace("\n", "\n  "));
        builder.append("goto ").append(endifLabel).append(";\n").append(thenLabel).append(":");
        builder.append(("\n" + this.thenStatement.getOllir().strip()).replace("\n", "\n  "));
        builder.append("\n").append(endifLabel).append(":");

        return builder.toString();
    }

    public static If fromNode(SymbolTable table, Method currentMethod, JmmNode node) throws JmmException {
        JmmNode conditionNode = node.getChildren().get(0).getChildren().get(0);
        JmmNode thenNode = node.getChildren().get(1).getChildren().get(0);
        JmmNode elseNode = node.getChildren().get(2).getChildren().get(0);

        Value conditionValue = Value.fromNode(table, currentMethod, conditionNode, Program.BOOL_TYPE);
        Statement thenStatement = Statement.fromNode(table, currentMethod, thenNode);
        Statement elseStatement = Statement.fromNode(table, currentMethod, elseNode);

        // the if condition needs to be a boolean
        if (!conditionValue.getReturnType().equals(Program.BOOL_TYPE))
            throw JmmException.invalidCondition(node, conditionValue.getReturnType());

        return new If(table, currentMethod, conditionValue, thenStatement, elseStatement);
    }
}
