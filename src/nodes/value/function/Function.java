package nodes.value.function;

import nodes.Method;
import nodes.SymbolTable;
import nodes.value.Terminal;
import nodes.value.Value;
import nodes.value.exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.List;

public abstract class Function extends Value {
    protected SymbolTable symbolTable = null;
    protected JmmNode node = null;

    protected String methodClass = null;
    protected String methodName = null;
    private Method method = null;

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    protected abstract List<Value> getArguments();

    @Override
    public Type getReturnType() {
        if (this.method == null)
            return null;
        return this.method.getReturnType();
    }

    // ----------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------

    protected void setMethod(SymbolTable table) {
        if (methodName == null)
            return;
        if (methodClass == null)
            methodClass = table.getClassName();
        this.method = table.getMethod(methodName, methodName);
    }

    // ----------------------------------------------------------------
    // Static functions for expression creation
    // ----------------------------------------------------------------

    public static Function fromNode(SymbolTable table, Method scopeMethod, JmmNode node, Type expectedReturn) throws JmmException {
        // Create respective objects
        Function function = switch (node.getKind()) {
            case "Construction" -> new Construction(table, node);
            case "Operation" -> new Operation(table, node);
            case "Call" -> new Call(table, scopeMethod, node, expectedReturn);
            default -> null;
        };
        if (function == null || function.method == null)
            return null;

        List<Terminal> parameters = function.method.getParameters();
        List<Value> arguments = function.getArguments();
        return null;
    }
}
