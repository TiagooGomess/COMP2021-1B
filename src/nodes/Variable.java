package nodes;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class Variable extends Symbol implements Expression {
    public Variable(Type type, String name) {
        super(type, name);
    }

    @Override
    public Type getReturnType() {
        return getType();
    }
}
