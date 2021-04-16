package analysis.value.function;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import analysis.value.*;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
import java.util.List;

public class Access extends Function {
    public Access(SymbolTable table, Method scopeMethod, JmmNode node) throws JmmException {
        this.table = table;
        this.node = node;
        this.scopeMethod = scopeMethod;

        // Method name and class
        this.methodName = "%" + node.getKind();
        this.setMethod();
    }

    @Override
    protected String getOutputName() {
        return "\"array access\" operator";
    }

    @Override
    protected List<JmmNode> getArguments() {
        List<JmmNode> arguments = new ArrayList<>();
        for (JmmNode child : this.node.getChildren())
            if (child.getKind().equals("Position"))
                arguments.add(child.getChildren().get(0));
            else
                arguments.add(child);
        return arguments;
    }

    public String getVariableName() {
        return getArguments().get(0).get("name");
    }

    @Override
    public String getOllir() {
        Value position = argumentValues.get(1);

        StringBuilder builder = new StringBuilder();

        builder.append(getVariableName());
        builder.append("[");
        Value.addValueToBuilder(builder, position, this.scopeMethod);
        builder.append("]");
        builder.append(".i32");

        return builder.toString();
    }
}
