package analysis.value.function;

import analysis.method.Method;
import analysis.symbol.Class;
import analysis.symbol.SymbolTable;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.ArrayList;
import java.util.List;

public class Construction extends Function {
    private final Class classObject;

    public Construction(SymbolTable table, Method scopeMethod, JmmNode node) throws JmmException {
        this.table = table;
        this.node = node;
        this.scopeMethod = scopeMethod;

        // Method name and class
        this.methodName = "%" + node.getKind();
        this.methodClassName = node.get("type");
        this.classObject = table.getClass(methodClassName);
        this.setMethod();
    }

    @Override
    protected String getOutputName() {
        return "\"constructor\" of \"" + this.methodClassName + "\"";
    }

    @Override
    protected List<JmmNode> getArguments() {
        for (JmmNode child : this.node.getChildren())
            if (child.getKind().equals("Size"))
                return child.getChildren();
        return new ArrayList<>();
    }

    @Override
    public String getOllir() {

        boolean isArray = false;
        if (!argumentValues.isEmpty())
            isArray = true;


        StringBuilder builder = new StringBuilder();

        builder.append("new").append("(");
        String typeOllir = typeToOllir(this.classObject.getReturnType());

        if (isArray) {
            builder.append("array, ");
            Value.addValueToBuilder(builder, argumentValues.get(0), this.scopeMethod);
        } else {
            builder.append(typeOllir.substring(1));
        }

        builder.append(")");
        builder.append(typeOllir);
        if (!isArray)
            builder.append(";");

        if (!isArray) {
            builder.append("invokespecial");
            builder.append("(");
            builder.append("%VariableName");
            builder.append(", \"<init>\").V");
        }

        return builder.toString();
    }
}
