package nodes.value.function;

import nodes.SymbolTable;
import nodes.value.Value;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.List;

public class Construction extends Function {
    public Construction(SymbolTable table, JmmNode node) {
        this.table = table;
        this.node = node;

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
    protected List<Value> getArguments() {
        return null;
    }
}
