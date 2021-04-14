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
    protected SymbolTable table = null;
    protected Method scopeMethod = null;
    protected JmmNode node = null;

    protected String methodClass = null;
    protected String methodName = null;
    private Method method = null;

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    protected abstract String getOutputName();

    protected abstract List<JmmNode> getArguments();

    @Override
    public Type getReturnType() {
        if (this.method == null)
            return null;
        return this.method.getReturnType();
    }

    // ----------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------

    protected void setMethod() {
        if (this.methodName == null)
            return;
        if (this.methodClass == null)
            this.method = this.table.getMethod(this.methodName);
        else
            this.method = this.table.getMethod(this.methodClass, this.methodName);
        System.out.println(this.methodName + " - " + this.methodClass);
    }

    // ----------------------------------------------------------------
    // Static functions for expression creation
    // ----------------------------------------------------------------

    public static Function fromNode(SymbolTable table, Method scopeMethod, JmmNode node, Type expectedReturn) throws JmmException {
        // Create respective objects
        Function function = switch (node.getKind()) {
            case "Construction" -> new Construction(table, scopeMethod, node);
            case "Operation" -> new Operation(table, scopeMethod, node);
            case "Call" -> new Call(table, scopeMethod, node, expectedReturn);
            default -> null;
        };
        if (function == null || function.method == null) {
            return null;
        }

        List<JmmNode> arguments = function.getArguments();
        List<Terminal> parameters = function.method.getParameters();
        if (parameters.size() != arguments.size())
            throw JmmException.invalidNumberOfArguments(function.getOutputName(), parameters.size(), arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            Terminal parameter = parameters.get(i);
            Value argument = Value.fromNode(table, scopeMethod, arguments.get(i), parameter.getReturnType());
            if (argument == null) {
                System.out.println(arguments.get(i));
            }
            if (!parameter.getReturnType().equals(argument.getReturnType()))
                throw JmmException.invalidTypeForArgument(function.methodName, parameter.getName(), parameter.getReturnType(), argument.getReturnType());
        }

        return function;
    }
}
