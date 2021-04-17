package analysis.statement;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
import java.util.List;

public class Block extends Statement {
    private final List<Statement> statements;

    private Block(SymbolTable table, Method method, List<Statement> statements) {
        this.table = table;
        this.method = method;
        this.statements = statements;
    }

    public String getOllir() {
        StringBuilder result = new StringBuilder();
        for (var statement : this.statements) {
            result.append(statement.getOllir()).append("\n");
        }

        return result.toString();
    }

    public static Block fromNode(SymbolTable table, Method currentMethod, JmmNode node) throws JmmException {
        List<JmmNode> children = node.getChildren();
        List<Statement> statements = new ArrayList<>();
        for (JmmNode child : children)
            statements.add(Statement.fromNode(table, currentMethod, child));
        return new Block(table, currentMethod, statements);
    }
}
