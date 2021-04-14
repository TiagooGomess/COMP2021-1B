package nodes.value.function;

import nodes.method.Method;
import nodes.SymbolTable;
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
    protected List<JmmNode> getArguments() {
        for (JmmNode child : this.node.getChildren())
            if (child.getKind().equals("Size"))
                return child.getChildren();
        return new ArrayList<>();
    }
}
