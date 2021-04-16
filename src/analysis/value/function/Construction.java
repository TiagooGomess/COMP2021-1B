package analysis.value.function;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
import java.util.List;

public class Construction extends Function {
    public Construction(SymbolTable table, Method scopeMethod, JmmNode node) throws JmmException {
        this.table = table;
        this.node = node;
        this.scopeMethod = scopeMethod;

        // Method name and class
        this.methodName = "%" + node.getKind();
        this.methodClassName = node.get("type");
        this.setMethod();
    }

    @Override
    protected String getOutputName() {
        return "\"constructor\" of \"" + this.methodClassName + "\"";
    }

    @Override
    protected List<JmmNode> getArguments() {
        for (JmmNode child : this.node.getChildren())
            if (child.getKind().equals("Size"))
                return child.getChildren();
        return new ArrayList<>();
    }

    @Override
    public String getOllir() {
        return null;
    }
}
