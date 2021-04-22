package analysis.statement;

import analysis.method.Method;
import analysis.symbol.Class;
import analysis.symbol.SymbolTable;
import analysis.value.function.Access;
import analysis.value.Terminal;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class Assignment extends Statement {
    private final Value variable;
    private final Value expression;

    private Assignment(SymbolTable table, Method method, Value variable, Value expression) {
        this.table = table;
        this.method = method;
        this.variable = variable;
        this.expression = expression;
    }

    public String getOllir() {
        Class mainClass = this.table.getClass(null);
        boolean isField;
        if (variable instanceof Terminal)
            isField = mainClass.isField((Terminal) variable);
        else
            isField = mainClass.isField(this.method.getVariable(((Access) variable).getVariableName()));

        StringBuilder builder = new StringBuilder();
        if (isField)
            builder.append("putfield(this, ");

        Value.addValueToBuilder(builder, variable, this.method, isField);

        if (isField) {
            builder.append(", ");
        } else {
            Type assignmentType = variable.getReturnType();
            builder.append(" :=");
            builder.append(Value.typeToOllir(assignmentType)).append(" ");
        }

        Value.addValueToBuilder(builder, expression, this.method);

        if (isField)
            builder.append(").V");

        builder.append(";");
        return builder.toString();
    }

    public static Assignment fromNode(SymbolTable table, Method currentMethod, JmmNode node) throws JmmException {
        JmmNode variableNode = node.getChildren().get(0);
        JmmNode expressionNode = node.getChildren().get(1);

        Value variable;
        if (variableNode.getKind().equals("Access"))
            variable = Access.fromNode(table, currentMethod, variableNode, null);
        else
            variable = Terminal.fromVariable(table, currentMethod, variableNode);

        assert variable != null;
        Value expression = Value.fromNode(table, currentMethod, expressionNode, variable.getReturnType());

        if (!variable.getReturnType().equals(expression.getReturnType()))
            throw JmmException.invalidAssignment(variable, expression.getReturnType());

        return new Assignment(table, currentMethod, variable, expression);
    }
}

