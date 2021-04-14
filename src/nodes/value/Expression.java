package nodes.value;

import nodes.Method;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.List;


public class Expression extends Value {
    private Method method;
    private List<Value> operands;

    protected Expression(Method method, List<Value> operands) {
        this.method = method;
        this.operands = operands;
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    @Override
    public Type getReturnType() {
        return this.method.getReturnType();
    }

    // ----------------------------------------------------------------
    // Static functions for expression creation
    // ----------------------------------------------------------------

}
