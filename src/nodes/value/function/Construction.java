package nodes.value.function;

import nodes.SymbolTable;
import pt.up.fe.comp.jmm.JmmNode;

public class Construction extends Function {
    public Construction(SymbolTable table, JmmNode node) {
        this.methodName = "%" + node.getKind();
        this.setMethod(table);
    }
}
