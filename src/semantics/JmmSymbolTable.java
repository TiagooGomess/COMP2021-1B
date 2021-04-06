import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class JmmSymbolTable implements SymbolTable {

    private List<String> imports = new ArrayList<String>();
    private String className = null;
    private String superClassName = null;
    private List<Symbol> fields = new ArrayList<>();
    private Map<String, JmmMethodSymbolTable> methods = new HashMap<>();

    public JmmSymbolTable() {

    }

    @Override
    public List<String> getImports() {
        return this.imports;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public String getSuper() {
        return this.superClassName;
    }

    @Override
    public List<Symbol> getFields() {
        return this.fields;
    }

    @Override
    public List<String> getMethods() {
        return new ArrayList<String>(this.methods.keySet());
    }

    @Override
    public Type getReturnType(String methodName) {
        return this.methods.get(methodName).getReturnType();
    }

    @Override
    public List<Symbol> getParameters(String methodName) {
        return this.methods.get(methodName).getParameters();
    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        return this.methods.get(methodName).getLocalVariables();
    }

    public void addImport(String importName) {
        this.imports.add(importName);
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSuper(String superClassName) {
        this.superClassName = superClassName;
    }

    public void addMethod(String methodName, Type returnType) {
        this.methods.put(methodName, new JmmMethodSymbolTable(returnType));
    }

    public void addField(Symbol field) {
        this.fields.add(field);
    }

    public void addParameter(String methodName, Symbol argument) {
        this.methods.get(methodName).addParameter(argument);
    }

    public void addLocalVariable(String methodName, Symbol localVariable) {
        this.methods.get(methodName).addLocalVariable(localVariable);
    }

    public String toString(String spaces) {
        StringBuilder b = new StringBuilder();

        b.append(spaces);
        b.append("Class: ");
        b.append(this.className);
        b.append("\n");

        b.append(spaces);
        b.append("Extends: ");
        b.append(this.superClassName);
        b.append("\n");

        if (this.imports.size() > 0) {
            b.append(spaces);
            b.append("Imports:\n");
            for (String importName : this.imports) {
                b.append(spaces);
                b.append("  ");
                b.append(importName);
                b.append("\n");
            }
        }

        if (this.fields.size() > 0) {
            b.append(spaces);
            b.append("Fields:\n");
            for (Symbol field : this.fields) {
                b.append(spaces);
                b.append("  ");
                b.append(field.getName() + " -> " + field.getType().toString());
                b.append("\n");
            }
        }

        if (this.methods.size() > 0) {
            b.append(spaces);
            b.append("Methods:\n");
            for (String method : this.methods.keySet()) {
                b.append(spaces);
                b.append("  ");
                b.append(method);
                b.append("\n");
                b.append(this.methods.get(method).toString(spaces + "    "));
            }
        }

        return b.toString();
    }

    public String toString() {
        return toString("");
    }
}