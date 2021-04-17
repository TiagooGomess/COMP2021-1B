package analysis.statement;

import analysis.method.Method;
import analysis.symbol.Program;
import analysis.symbol.SymbolTable;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

public class While extends Statement {
    public static int while_counter = 1;
    private final Value condition;
    private final Statement body;

    private While(SymbolTable table, Method method, Value condition, Statement body) {
        this.table = table;
        this.method = method;
        this.condition = condition;
        this.body = body;
    }

    public String getOllir() {
        String loopLabel = "Loop" + while_counter;
        String bodyLabel = "Body" + while_counter;
        String endLoopLabel = "EndLoop" + while_counter++;

        StringBuilder builder = new StringBuilder();

        builder.append(loopLabel + ":\n");

        // Condition
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append("if (");
        Value.addValueToBuilder(conditionBuilder, this.condition, this.method);
        conditionBuilder.append(") goto ").append(bodyLabel).append(";\ngoto ").append(endLoopLabel).append(";\n");

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append(bodyLabel).append(":\n");
        bodyBuilder.append("  ").append(this.body.getOllir().replace("\n", "\n  "));
        bodyBuilder.append("goto ").append(loopLabel).append(";");

        builder.append(conditionBuilder);
        builder.append(bodyBuilder);

        return builder.toString().replace("\n", "\n  ") + "\n" + endLoopLabel + ":";
    }

    public static While fromNode(SymbolTable table, Method currentMethod, JmmNode node) throws JmmException {
        JmmNode conditionNode = node.getChildren().get(0).getChildren().get(0);
        JmmNode statementNode = node.getChildren().get(1);

        Value conditionValue = Value.fromNode(table, currentMethod, conditionNode, Program.BOOL_TYPE);
        Statement statement = Statement.fromNode(table, currentMethod, statementNode);

        // the while condition needs to be a boolean
        if (!conditionValue.getReturnType().equals(Program.BOOL_TYPE))
            throw JmmException.invalidCondition(conditionValue.getReturnType());

        return new While(table, currentMethod, conditionValue, statement);
    }
}
