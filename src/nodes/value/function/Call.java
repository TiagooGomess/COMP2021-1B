package nodes.value.function;

import nodes.Method;
import nodes.SymbolTable;
import nodes.value.Value;
import nodes.value.exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class Call extends Function {
    private final Type expectedReturn;
    private JmmNode argumentsNode = null;

    public Call(SymbolTable table, Method scopeMethod, JmmNode node, Type expectedReturn) throws JmmException {
        this.table = table;
        this.node = node;
        this.scopeMethod = scopeMethod;
        this.expectedReturn = expectedReturn;

        // Method name, arguments node and class
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Method")) {
                this.methodName = child.get("name");
                for (JmmNode grandchild : child.getChildren())
                    if (grandchild.getKind().equals("Arguments"))
                        this.argumentsNode = grandchild;
            } else {
                Type returnType = Value.fromNode(table, scopeMethod, child, null).getReturnType();
                String name = returnType.getName();
                this.methodClass = returnType.isArray() ? name + "[]" : name;
            }
        }
        this.setMethod();
    }

    @Override
    protected String getOutputName() {
        return "method \"" + this.methodName + "\" of class \"" + this.methodClass + "\"";
    }

    @Override
    protected List<Value> getArguments() throws JmmException {
        List<Value> arguments = new ArrayList<>();
        if (this.argumentsNode == null)
            return arguments;
        int i = 0;
        for (JmmNode child : this.argumentsNode.getChildren())
            arguments.add(Value.fromNode(this.table, this.scopeMethod, child, getParameterType(i++)));
        return arguments;
    }
}
