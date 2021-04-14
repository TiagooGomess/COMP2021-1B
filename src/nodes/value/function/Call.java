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
    private final Method scopeMethod;

    public Call(SymbolTable table, Method scopeMethod, JmmNode node, Type expectedReturn) throws JmmException {
        this.symbolTable = table;
        this.node = node;
        this.scopeMethod = scopeMethod;
        this.expectedReturn = expectedReturn;

        // Method name and class
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Method"))
                this.methodName = child.get("name");
            else
                this.methodClass = Value.fromNode(table, scopeMethod, child, null).getReturnType().getName();
        }
        this.setMethod(table);
    }

    @Override
    protected List<Value> getArguments() {
        return null;
    }
}
