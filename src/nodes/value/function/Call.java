package nodes.value.function;

import nodes.SymbolTable;
import pt.up.fe.comp.jmm.JmmNode;

public class Call extends Function {
    public Call(SymbolTable table, JmmNode node) {
        // Method name
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Method")) {
                this.methodName = child.get("name");
                break;
            }
        }
        this.setMethod(table);
    }
}
