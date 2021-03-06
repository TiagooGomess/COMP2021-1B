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
    private Class methodClass = null;
    private List<Method> methods = null;
    protected Method method = null;
    protected List<Value> argumentValues = new ArrayList<>();

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
            methodClass = this.table.getClass(this.methodClassName);

            // If its the main class of the Jmm file we are parsing
            if (methodClass == null || (this.table.getClassName().equals(methodClass.getName()) && this.table.getSuper() == null)) {
                throw JmmException.invalidMethod(this.node, this.methodName);
            }

            // Create new method by inference
            this.methods = Collections.singletonList(createMethodByInference(table, methodClass, call, getNewParameters()));
        }
    }

    public void checkInitiatedVariable() throws JmmException {
        for (Value value : this.argumentValues) {
            if (value instanceof Terminal) {
                Terminal terminal = (Terminal) value;
                if (!terminal.isInitiated() && !terminal.isLiteral() && !terminal.isParameter())
                    throw JmmException.uninitializedVariable(this.node, terminal.getName());
            }
        }
    }

    // ----------------------------------------------------------------
    // Static functions for expression creation
    // ----------------------------------------------------------------

    public static Method createMethodByInference(SymbolTable table, Class methodClass, Call call, List<Terminal> types) {
        Method inferred = new Method(methodClass, call.methodName, call.expectedReturn, types, call.objectCalling instanceof Class);
        if (methodClass == null)
            methodClass = table.getClass(null);
        try {
            methodClass.addMethod(inferred);
        } catch (Exception e) {

        }
        return inferred;
    }

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
        if (possibleParameterLists.isEmpty() && table.getSuper() == null) {
            throw JmmException.invalidNumberOfArguments(node, function.getOutputName(), arguments.size());
        }

        Map<Method, List<Value>> methodTypeList = new HashMap<>();
        for (int i = 0; i < arguments.size(); i++) {

            Map<Method, List<Terminal>> newParameterLists = new HashMap<>();

            for (Method method : possibleParameterLists.keySet()) {

                List<Terminal> possibleParameterList = possibleParameterLists.get(method);
                Terminal parameter = possibleParameterList.get(i);
                Value value;
                value = Value.fromNode(table, scopeMethod, arguments.get(i), possibleParameterList.get(i).getReturnType());

                if (parameter.getReturnType().equals(value.getReturnType()))
                    newParameterLists.put(method, possibleParameterList);

                if (!methodTypeList.containsKey(method))
                    methodTypeList.put(method, new ArrayList<>());
                methodTypeList.get(method).add(value);
            }

            possibleParameterLists = newParameterLists;
            if (possibleParameterLists.isEmpty())
                break;
        }


        boolean neededInferredMethod = false;
        if (possibleParameterLists.keySet().size() != 1) {
            if (possibleParameterLists.keySet().isEmpty() && function instanceof Call) {
                List<Terminal> parameters = function.getNewParameters();
                Method inferredMethod = createMethodByInference(table, function.methodClass, (Call) function, parameters);
                possibleParameterLists.put(inferredMethod, null);
                neededInferredMethod = true;
            }
            if (table.getSuper() == null) {
                for (Method method : methodTypeList.keySet())
                    throw JmmException.invalidTypeForArguments(node, function.getOutputName(), methodTypeList.get(method));
            }
        }

        for (Method method : possibleParameterLists.keySet()) {
            function.method = method;
        }

        function.argumentValues = methodTypeList.get(function.method);
        if (function.argumentValues == null) {
            if (!neededInferredMethod) {
                function.argumentValues = new ArrayList<>();
            } else {
                List<Value> methodTypeListForInferred = new ArrayList<>();
                for (JmmNode argument : arguments)
                    methodTypeListForInferred.add(Value.fromNode(table, scopeMethod, argument, null));
                function.argumentValues = methodTypeListForInferred;
            }
        }

        if (function instanceof Call) {
            Call call = (Call) function;
            // Update method if found the type of it
            if (function.getReturnType() == null)
                function.method.setReturnType(call.expectedReturn);

            // Varify if object or class reference
            if ((function.method.isStatic() && !(call.objectCalling instanceof Class))
                    || (!function.method.isStatic() && (call.objectCalling instanceof Class))) {
                throw JmmException.invalidCaller(node, function.method.getName(), call.objectCalling);
            }
        }

        function.checkInitiatedVariable();
        return function;
    }
}
