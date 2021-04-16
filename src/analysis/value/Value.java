package analysis.value;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import exception.JmmException;
import analysis.value.function.Construction;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

// public String[] jjtNodeName = { "skipParenthesis", "Program",
// "ImportDeclaration", "void", "Class", "Attributes",
// "Methods", "MethodDeclaration", "Arguments", "Argument", "Body", "Return",
// "VariableDeclaration",
// "AttributeDeclaration", "Block", "If", "Then", "Else", "While", "Assignment",
// "Condition", "Operation",
// "Position", "Access", "Call", "Method", "Construction", "Size", "Literal",
// "Variable", "This",
// "Operator", };

public abstract class Value {
    public abstract Type getReturnType();

    public abstract String getOllir();

    public static String typeToOllir(Type type) {
        StringBuilder builder = new StringBuilder();

        // Is array?
        if (type.isArray())
            builder.append(".array");

        // Name of type
        String typeName = type.getName();
        switch (typeName) {
            case "int" -> builder.append(".i32");
            case "boolean" -> builder.append(".bool");
            case "void" -> builder.append(".V");
            default -> builder.append(".").append(typeName);
        }

        return builder.toString();
    }

    // ----------------------------------------------------------------
    // Static functions for value creation
    // ----------------------------------------------------------------

    public static Value fromNode(SymbolTable table, Method scopeMethod, JmmNode node, Type expectedReturn) throws JmmException {
        Value result = switch (node.getKind()) {
            case "Literal" -> Terminal.fromLiteral(node);
            case "Variable", "This" -> Terminal.fromVariable(table, scopeMethod, node);
            case "Access", "Call", "Construction", "Operation" -> Construction.fromNode(table, scopeMethod, node, expectedReturn);
            default -> null;
        };
        if (result == null)
            System.out.println(node.getKind());
        return result;
    }
}