package analysis.value.function;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import analysis.value.Terminal;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Operation extends Function {
    public Operation(SymbolTable table, Method scopeMethod, JmmNode node) throws JmmException {
        this.table = table;
        this.node = node;
        this.scopeMethod = scopeMethod;

        // Method name
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Operator")) {
                this.methodName = "%" + child.get("name");
                break;
            }
        }
        this.setMethod();
    }

    @Override
    protected String getOutputName() {
        return "\"" + this.methodName.substring(1).toLowerCase() + "\" operator";
    }

    @Override
    protected List<JmmNode> getArguments() {
        List<JmmNode> arguments = new ArrayList<>();
        for (JmmNode child : node.getChildren())
            if (!child.getKind().equals("Operator"))
                arguments.add(child);
        return arguments;
    }

    @Override
    public String getOllir() {
        StringBuilder result = new StringBuilder();
        Value leftOperand = this.argumentValues.get(0);
        Value rightOperand = this.argumentValues.size() > 1 ? this.argumentValues.get(1) : this.argumentValues.get(0);

        String leftOllir = leftOperand.getOllir();
        String rightOllir = rightOperand.getOllir();

        String operator = switch (this.methodName.substring(1)) {
            case "Addition" -> "+.i32";
            case "Subtraction" -> "-.i32";
            case "Multiplication" -> "*.i32";
            case "Division" -> "/.i32";
            case "Comparison" -> "<.i32";
            case "Negation" -> "!.bool";
            case "Conjunction" -> "&&.bool";
            default -> null;
        };

        if (leftOperand instanceof Terminal) {
            result.append(leftOllir);
        } else {
            addValueToBuilder(result, leftOperand, leftOllir);
        }

        result.append(" ").append(operator).append(" ");

        if (rightOperand instanceof Terminal) {
            result.append(rightOllir);
        } else {
            addValueToBuilder(result, rightOperand, rightOllir);
        }

        return result.toString();
    }

    private void addValueToBuilder(StringBuilder result, Value operand, String ollir) {
        Terminal terminal = new Terminal(operand.getReturnType(), "t" + SymbolTable.auxiliaryVariableNumber++);
        ArrayList<String> childLines = new ArrayList<>(Arrays.asList(ollir.split("\n")));
        String lastLine = childLines.get(childLines.size() - 1);
        childLines.remove(childLines.size() - 1);
        String assignmentType = Value.typeToOllir(operand.getReturnType());
        result.insert(0, terminal.getOllir() + " :=" + assignmentType + " " + lastLine + ";\n");
        if (!childLines.isEmpty())
            result.insert(0, String.join("\n", childLines) + "\n");
        result.append(terminal.getOllir());
    }


}
