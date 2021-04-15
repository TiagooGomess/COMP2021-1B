package analysis.value.function;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class Call extends Function {
    protected final Type expectedReturn;
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
                this.methodClassName = returnType.isArray() ? name + "[]" : name;
            }
        }
        this.setMethod();
    }

    @Override
    protected String getOutputName() {
        return "method \"" + this.methodName + "\" of class \"" + this.methodClassName + "\"";
    }

    @Override
    protected List<JmmNode> getArguments() {
        if (this.argumentsNode != null) {
            List<JmmNode> arguments = this.argumentsNode.getChildren();
            if (arguments != null)
                return arguments;
        }
        return new ArrayList<>();
    }
}