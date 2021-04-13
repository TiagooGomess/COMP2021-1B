package nodes;

import pt.up.fe.comp.jmm.analysis.table.Type;

public abstract class BooleanExpression implements Expression {
    private static final Type returnType = new Type("boolean", false);

    @Override
    public Type getReturnType() {
        return returnType;
    }
}
