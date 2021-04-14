package nodes.expression;

import nodes.Method;
import nodes.SymbolTable;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
import java.util.List;


public class Expression {
    private static Value checkOperation(SymbolTable table, Method scopeMethod, JmmNode node) {
        String operationName = null;
        List<JmmNode> operands = new ArrayList<>();
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Operator"))
                operationName = "%" + child.get("name");
            else
                operands.add(child);
        }
        Method function = table.getMethod(operationName);
        System.out.println(function);
        return null;
    }

    public static Value fromNode(SymbolTable table, Method scopeMethod, JmmNode node) {
        return switch (node.getKind()) {
            case "Literal" -> Terminal.fromLiteral(node);
            case "Variable" -> Terminal.fromVariable(scopeMethod, node);
            case "Operation" -> checkOperation(table, scopeMethod, node);
            default -> null;
        };
    }
}
