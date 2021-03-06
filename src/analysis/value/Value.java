package analysis.value;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import analysis.value.function.Access;
import analysis.value.function.Call;
import exception.JmmException;
import analysis.value.function.Construction;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Value {

    public static Terminal addValueToBuilder(StringBuilder result, SymbolTable table, Value operand, Method method, boolean fromPutField, boolean assignmentVariable) {
        String ollir = operand.getOllir();

        Terminal terminal = null;
        Terminal terminalOperand = null;
        if (operand instanceof Terminal) {
            terminalOperand = (Terminal) operand;
            int argumentNumber = method.getArgumentNumber(terminalOperand);
            if (argumentNumber != 0)
                result.append("$").append(argumentNumber).append(".");
            else if (!fromPutField && table.getClass(null).isField(terminalOperand) && !terminalOperand.getName().equals("this")) {
                terminal = new Terminal(operand.getReturnType(), "aux" + SymbolTable.auxiliaryVariableNumber++);
            }

            if (terminal == null) {
                result.append(ollir);
                return null;
            }
        }

        if (terminal == null) {
            if (!assignmentVariable)
                terminal = new Terminal(operand.getReturnType(), "aux" + SymbolTable.auxiliaryVariableNumber++);
        } else {
            String typeOllir = Value.typeToOllir(terminalOperand.getReturnType());
            ollir = "getfield(this, " + terminalOperand.getOllir() + ")" + typeOllir + "\n";
        }

        ArrayList<String> childLines = new ArrayList<>(Arrays.asList(ollir.split("\n")));
        String lastLine = childLines.get(childLines.size() - 1);
        if (!assignmentVariable)
            lastLine = lastLine.replace("%VariableName", terminal.getOllir()).replace(";invokespecial", ";\ninvokespecial");
        childLines.remove(childLines.size() - 1);
        String assignmentType = Value.typeToOllir(operand.getReturnType());
        if (!assignmentVariable)
            result.insert(0, terminal.getOllir() + " :=" + assignmentType + " " + lastLine + ";\n");
        if (!childLines.isEmpty())
            result.insert(0, String.join("\n", childLines) + "\n");
        if (!assignmentVariable)
            result.append(terminal.getOllir());
        else
            result.append(lastLine);

        return terminal;
    }

    public static Terminal addValueToBuilder(StringBuilder result, SymbolTable table, Value operand, Method method) {
        return addValueToBuilder(result, table, operand, method, false, false);
    }

    public static Terminal addValueToAssignmentBuilder(StringBuilder result, SymbolTable table, Value operand, Method method, boolean fromPutField) {
        return addValueToBuilder(result, table, operand, method, fromPutField, true);
    }

    public abstract Type getReturnType();

    public abstract String getOllir();

    public static String typeToOllir(Type type) {
        StringBuilder builder = new StringBuilder();

        if (type == null)
            return ".V";

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
        boolean isLiteral = false;
        Value result;
        switch (node.getKind()) {
            case "Literal" -> {
                result = Terminal.fromLiteral(table, node);
                isLiteral = true;
            }
            case "Variable", "This" -> result = Terminal.fromVariable(table, scopeMethod, node, expectedReturn);
            case "Access", "Call", "Construction", "Operation" -> result = Construction.fromNode(table, scopeMethod, node, expectedReturn);
            default -> result = null;
        }
        if (result == null)
            System.out.println("null value result: " + node.getKind());

        if (isLiteral) {
            ((Terminal) result).setIsLiteral();
        }

        return result;
    }
}