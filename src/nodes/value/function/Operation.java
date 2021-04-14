package nodes.value.function;

import nodes.Method;
import nodes.SymbolTable;
import nodes.value.Value;
import nodes.value.exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
import java.util.List;

public class Operation extends Function {
    public Operation(SymbolTable table, Method scopeMethod, JmmNode node) {
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
        return this.methodName.substring(1).toLowerCase() + " operator";
    }

    @Override
    protected List<Value> getArguments() throws JmmException {
        List<Value> arguments = new ArrayList<>();
        int i = 0;
        for (JmmNode child : node.getChildren())
            if (!child.getKind().equals("Operator"))
                arguments.add(Value.fromNode(this.table, this.scopeMethod, child, this.getParameterType(i++)));
        return arguments;
    }
}
