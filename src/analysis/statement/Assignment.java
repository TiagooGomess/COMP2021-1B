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
    private Value variable;
    private final Value expression;

    private Assignment(SymbolTable table, Method method, Value variable, Value expression) {
        this.table = table;
        this.method = method;
        this.variable = variable;
        this.expression = expression;
        setInitiated();
    }

    private void setInitiated() {
        if (variable instanceof Terminal) {
            ((Terminal) variable).setInitiated();
        }
    }

    public String getOllir() {
        Class mainClass = this.table.getClass(null);
        boolean isField;
        if (variable instanceof Terminal)
            isField = mainClass.isField((Terminal) variable);
        else
            isField = mainClass.isField(this.method.getVariable(((Access) variable).getVariableName()));

        StringBuilder builder = new StringBuilder();
        if (isField) {
            if (variable instanceof Access) {
                Access access = (Access) variable;
                Terminal terminal = (Terminal) access.getVariable();
                Terminal aux = new Terminal(terminal.getReturnType(), "aux" + SymbolTable.auxiliaryVariableNumber++);
                variable = new Access(table, this.method, aux, access.getPosition());
                builder.append(aux.getOllir()).append(" :=");
                builder.append(Value.typeToOllir(aux.getReturnType())).append(" ");
                builder.append("getfield(this, ").append(terminal.getOllir()).append(")");
                builder.append(Value.typeToOllir(terminal.getReturnType())).append(";\n");
                isField = false;
            } else {
                builder.append("putfield(this, ");
            }
        }

        Value.addValueToAssignmentBuilder(builder, table, variable, this.method, isField);

        if (isField) {
            builder.append(", ");
        } else {
            Type assignmentType = variable.getReturnType();
            builder.append(" :=");
            builder.append(Value.typeToOllir(assignmentType)).append(" ");
        }

        Value.addValueToBuilder(builder, this.table, expression, this.method);

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
            variable = Terminal.fromVariable(table, currentMethod, variableNode, null);

        assert variable != null;
        Value expression = Value.fromNode(table, currentMethod, expressionNode, variable.getReturnType());

        if (variable instanceof Terminal && variable.getReturnType() == null)
            ((Terminal) variable).setType(expression.getReturnType());
        else if (!variable.getReturnType().equals(expression.getReturnType()))
            throw JmmException.invalidAssignment(node, variable, expression.getReturnType());

        return new Assignment(table, currentMethod, variable, expression);
    }
}

