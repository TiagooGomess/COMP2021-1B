package nodes.value.function;

import nodes.SymbolTable;
import pt.up.fe.comp.jmm.JmmNode;

public class Operation extends Function {
    public Operation(SymbolTable table, JmmNode node) {
        // Method name
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Operator")) {
                this.methodName = "%" + child.get("name");
                break;
            }
        }
        this.setMethod(table);
    }
}
