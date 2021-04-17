package analysis.value.function;

import analysis.method.Method;
import analysis.symbol.SymbolTable;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.List;

public class Call extends Function {
    protected final Type expectedReturn;
    private Value objectCalling;
    private JmmNode argumentsNode = null;

    public Call(SymbolTable table, Method scopeMethod, JmmNode node, Type expectedReturn) throws JmmException {
        this.table = table;
        this.node = node;
        this.scopeMethod = scopeMethod;
        this.expectedReturn = expectedReturn;

        // Method name, arguments node and class
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Method")) {
                this.methodName = child.get("name");
                for (JmmNode grandchild : child.getChildren())
                    if (grandchild.getKind().equals("Arguments"))
                        this.argumentsNode = grandchild;
            } else {
                objectCalling = Value.fromNode(table, scopeMethod, child, null);
                String name = objectCalling.getReturnType().getName();
                this.methodClassName = objectCalling.getReturnType().isArray() ? name + "[]" : name;
            }
        }
        this.setMethod();
    }

    @Override
    protected String getOutputName() {
        return "method \"" + this.methodName + "\" of class \"" + this.methodClassName + "\"";
    }

    @Override
    protected List<JmmNode> getArguments() {
        if (this.argumentsNode != null) {
            List<JmmNode> arguments = this.argumentsNode.getChildren();
            if (arguments != null)
                return arguments;
        }
        return new ArrayList<>();
    }

    @Override
    public String getOllir() {
        StringBuilder result = new StringBuilder();
        boolean length = false;

        if (this.methodName.equals("length")) {
            result.append("arraylength");
            length = true;
        } else if (this.method.isStatic()) {
            result.append("invokestatic");
        } else {
            result.append("invokevirtual");
        }

        result.append("(");

        if (!length) {
            if (this.method.isStatic())
                result.append(Value.typeToOllir(this.objectCalling.getReturnType()).substring(1));
            else
                addValueToBuilder(result, objectCalling, this.scopeMethod);
            result.append(", \"").append(this.methodName).append("\"");
            for (Value argument : this.argumentValues) {
                result.append(", ");
                addValueToBuilder(result, argument, this.scopeMethod);
            }
        }

        result.append(")");
        result.append(Value.typeToOllir(this.method.getReturnType()));

        return result.toString();
    }
}
