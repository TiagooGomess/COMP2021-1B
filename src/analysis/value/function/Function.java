package analysis.value.function;

import analysis.method.Method;
import analysis.symbol.Class;
import analysis.symbol.SymbolTable;
import analysis.value.Terminal;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public abstract class Function extends Value {
    protected SymbolTable table = null;
    protected Method scopeMethod = null;
    protected JmmNode node = null;

    protected String methodClassName = null;
    protected String methodName = null;
    private Method method = null;

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    protected abstract String getOutputName();

    protected abstract List<JmmNode> getArguments();

    private List<Terminal> getNewParameters() throws JmmException {
        List<Terminal> result = new ArrayList<>();

        var arguments = getArguments();
        int i = 0;
        for (var argument : arguments) {
            Value value = Value.fromNode(this.table, this.scopeMethod, argument, null);
            result.add(new Terminal(value.getReturnType(), "argument " + i++));
        }

        return result;
    }

    @Override
    public Type getReturnType() {
        if (this.method == null)
            return null;
        return this.method.getReturnType();
    }

    // ----------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------

    protected void setMethod() throws JmmException {
        if (this.methodName == null)
            return;

        if (this.methodClassName == null)
            this.method = this.table.getMethod(this.methodName);
        else
            this.method = this.table.getMethod(this.methodClassName, this.methodName);

        if (this.method == null) {
            Call call = (Call) this;
            Class methodClass = this.table.getClass(this.methodClassName);

            // Create new method by inference
            Method toAdd = new Method(call.methodName, call.expectedReturn, getNewParameters());
            methodClass.addMethod(toAdd);
            this.method = toAdd;
        } else if (this.method.getReturnType() == null) {
            if (this instanceof Call) {
                Call call = (Call) this;
                this.method.setReturnType(call.expectedReturn);
            }
        }
    }

    // ----------------------------------------------------------------
    // Static functions for expression creation
    // ----------------------------------------------------------------

    public static Function fromNode(SymbolTable table, Method scopeMethod, JmmNode node, Type expectedReturn) throws JmmException {
        // Create respective objects
        Function function = switch (node.getKind()) {
            case "Access" -> new Access(table, scopeMethod, node);
            case "Construction" -> new Construction(table, scopeMethod, node);
            case "Operation" -> new Operation(table, scopeMethod, node);
            case "Call" -> new Call(table, scopeMethod, node, expectedReturn);
            default -> null;
        };
        if (function == null || function.method == null)
            return null;

        List<JmmNode> arguments = function.getArguments();
        List<Terminal> parameters = function.method.getParameters();
        if (parameters.size() != arguments.size())
            throw JmmException.invalidNumberOfArguments(function.getOutputName(), parameters.size(), arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            Terminal parameter = parameters.get(i);
            Value argument = Value.fromNode(table, scopeMethod, arguments.get(i), parameter.getReturnType());
            if (!parameter.getReturnType().equals(argument.getReturnType()))
                throw JmmException.invalidTypeForArgument(function.getOutputName(), parameter.getName(), parameter.getReturnType(), argument.getReturnType());
        }

        return function;
    }
}
