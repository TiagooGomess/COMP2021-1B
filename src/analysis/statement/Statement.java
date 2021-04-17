package analysis.statement;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

public abstract class Statement {
    protected SymbolTable table;
    protected Method method;

    public abstract String getOllir();

    public static Statement fromNode(SymbolTable table, Method scopeMethod, JmmNode node) throws JmmException {
        return switch (node.getKind()) {
            case "Assignment" -> Assignment.fromNode(table, scopeMethod, node);
            case "Block" -> Block.fromNode(table, scopeMethod, node);
            case "If" -> If.fromNode(table, scopeMethod, node);
            case "Return" -> Return.fromNode(table, scopeMethod, node);
            case "While" -> While.fromNode(table, scopeMethod, node);
            default -> Expression.fromNode(table, scopeMethod, node);
        };
    }
}
