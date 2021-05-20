package analysis.value.function;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import analysis.value.Terminal;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
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
        Value rightOperand = this.argumentValues.size() > 1 ? this.argumentValues.get(1) : null;

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

        Terminal aux = addValueToBuilder(result, this.table, leftOperand, this.scopeMethod);
        result.append(" ").append(operator).append(" ");
        if (rightOperand != null) {
            addValueToBuilder(result, this.table, rightOperand, this.scopeMethod);
        } else if (aux == null) {
            addValueToBuilder(result, this.table, leftOperand, this.scopeMethod);
        } else {
            addValueToBuilder(result, this.table, aux, this.scopeMethod);
        }

        return result.toString();
    }
}
