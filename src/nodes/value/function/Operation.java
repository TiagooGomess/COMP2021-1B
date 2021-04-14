package nodes.value.function;

import nodes.SymbolTable;
import nodes.value.Value;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.List;

public class Operation extends Function {
    public Operation(SymbolTable table, JmmNode node) {
        this.symbolTable = table;
        this.node = node;

        // Method name
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Operator")) {
                this.methodName = "%" + child.get("name");
                break;
            }
        }
        this.setMethod(table);
    }

    @Override
    protected List<Value> getArguments() {
        return null;
    }
}
