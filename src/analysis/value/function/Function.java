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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Function extends Value {
    protected SymbolTable table = null;
    protected Method scopeMethod = null;
    protected JmmNode node = null;

    protected String methodClassName = null;
    protected String methodName = null;
    private List<Method> methods = null;
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
            this.methods = this.table.getMethod(this.methodName);
        else
            this.methods = this.table.getMethod(this.methodClassName, this.methodName);

        // If the method is not found anywhere in the symbol table
        if (this.methods == null || this.methods.isEmpty()) {
            // The only type of function node that can not be found is a call
            Call call = (Call) this;
            Class methodClass = this.table.getClass(this.methodClassName);

            // If its the main class of the Jmm file we are parsing
            if (methodClass == null || (this.table.getClassName().equals(methodClass.getName()) && this.table.getSuper() == null)) {
                throw JmmException.invalidMethod(this.methodName);
            }

            // Create new method by inference
            Method toAdd = new Method(call.methodName, call.expectedReturn, getNewParameters());
            methodClass.addMethod(toAdd);
            this.methods = Collections.singletonList(toAdd);
        }

        /*else if (this.methods.getReturnType() == null) {
            if (this instanceof Call) {
                Call call = (Call) this;
                this.methods.setReturnType(call.expectedReturn);
            }
        }*/
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
        if (function == null || function.methods == null)
            return null;

        List<JmmNode> arguments = function.getArguments();

        // Get list of possible parameters
        Map<Method, List<Terminal>> possibleParameterLists = new HashMap<>();
        for (Method method : function.methods)
            if (method.getParameters().size() == arguments.size())
                possibleParameterLists.put(method, method.getParameters());

        // If no method has the same number of arguments
        if (possibleParameterLists.isEmpty())
            throw JmmException.invalidNumberOfArguments(function.getOutputName(), arguments.size());

        List<Type> typeList = new ArrayList<>();
        for (int i = 0; i < arguments.size(); i++) {
            Map<Method, List<Terminal>> newParameterLists = new HashMap<>();

            for (Method method : possibleParameterLists.keySet()) {
                List<Terminal> possibleParameterList = possibleParameterLists.get(method);
                Terminal parameter = possibleParameterList.get(i);
                Value value;
                try {
                    value = Value.fromNode(table, scopeMethod, arguments.get(i), possibleParameterList.get(i).getReturnType());
                } catch (JmmException e) {
                    continue;
                }

                if (typeList.size() < i + 1)
                    typeList.add(value.getReturnType());
                else
                    typeList.set(i, value.getReturnType());

                if (parameter.getReturnType().equals(value.getReturnType()))
                    newParameterLists.put(method, possibleParameterList);
            }
            possibleParameterLists = newParameterLists;
            if (possibleParameterLists.isEmpty())
                break;
        }

        // TODO: add method by inference
        if (possibleParameterLists.keySet().size() != 1)
            throw JmmException.invalidTypeForArguments(function.getOutputName(), typeList);


        for (Method method : possibleParameterLists.keySet())
            function.method = method;

        return function;
    }
}