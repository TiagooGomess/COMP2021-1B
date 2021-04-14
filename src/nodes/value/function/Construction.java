package nodes.value.function;

import nodes.Method;
import nodes.SymbolTable;
import nodes.value.Value;
import nodes.value.exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
import java.util.List;

public class Construction extends Function {
    public Construction(SymbolTable table, Method scopeMethod, JmmNode node) {
        this.table = table;
        this.node = node;
        this.scopeMethod = scopeMethod;

        // Method name and class
        this.methodName = "%" + node.getKind();
        this.methodClass = node.get("type");
        this.setMethod();
    }

    @Override
    protected String getOutputName() {
        return "constructor of \"" + this.methodClass + "\"";
    }

    @Override
    protected List<Value> getArguments() throws JmmException {
        List<Value> arguments = new ArrayList<>();
        for (JmmNode child : this.node.getChildren())
            arguments.add(Value.fromNode(this.table, this.scopeMethod, child, null));
        return arguments;
    }
}
