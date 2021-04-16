package analysis.value;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import exception.JmmException;
import analysis.value.function.Construction;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.Arrays;

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

    public static Terminal addValueToBuilder(StringBuilder result, Value operand, Method method) {
        String ollir = operand.getOllir();

        if (operand instanceof Terminal) {
            int argumentNumber = method.getArgumentNumber((Terminal) operand);
            if (argumentNumber != 0)
                result.append("$").append(argumentNumber).append(".");
            result.append(ollir);
            return null;
        }

        Terminal terminal = new Terminal(operand.getReturnType(), "aux" + SymbolTable.auxiliaryVariableNumber++);
        if (ollir == null)
            ollir = "";
        ArrayList<String> childLines = new ArrayList<>(Arrays.asList(ollir.split("\n")));
        String lastLine = childLines.get(childLines.size() - 1);
        childLines.remove(childLines.size() - 1);
        String assignmentType = Value.typeToOllir(operand.getReturnType());
        result.insert(0, terminal.getOllir() + " :=" + assignmentType + " " + lastLine + ";\n");
        if (!childLines.isEmpty())
            result.insert(0, String.join("\n", childLines) + "\n");
        result.append(terminal.getOllir());

        return terminal;
    }

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