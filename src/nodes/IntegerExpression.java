package nodes;

import pt.up.fe.comp.jmm.analysis.table.Type;

public abstract class IntegerExpression implements Expression {
    private static final Type returnType = new Type("int", false);

    @Override
    public Type getReturnType() {
        return returnType;
    }
}
