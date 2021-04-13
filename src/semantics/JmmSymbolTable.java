package semantics;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class JmmSymbolTable implements SymbolTable {

    private String className = null;
    private String superClassName = null;
    private final List<String> imports = new ArrayList<>();
    private final List<Symbol> fields = new ArrayList<>();
    private final Map<String, JmmMethodSymbolTable> methods = new HashMap<>();
    private final List<Report> reports = new ArrayList<>();

    public JmmSymbolTable() {

    }

    public List<Report> getReports() {
        return this.reports;
    }

    public void addError(Exception e) {
        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, e.getStackTrace()[0].getLineNumber(), e.getMessage()));
    }

    public void addWarning(Exception e) {
        reports.add(new Report(ReportType.WARNING, Stage.SEMANTIC, e.getStackTrace()[0].getLineNumber(), e.getMessage()));
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
        return new ArrayList<>(this.methods.keySet());
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
        if (this.imports.contains(importName)){
            Exception e = new Exception("The import with name \"" + importName + "\" is already declared");
            this.addWarning(e);
        }
        this.imports.add(importName);
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSuper(String superClassName) {
        if (superClassName.equals(this.className)){
            Exception e = new Exception("The class \"" + superClassName + "\" cannot extend itself");
            this.addError(e);
        }
        this.superClassName = superClassName;
    }

    /*
    public void addMethod(String methodName, List<Type> arguments, Type returnType) {
    StringBuilder methodKey = new StringBuilder(methodName);
        for (Symbol symbol : parameters) {
            methodKey.append(" ").append(symbol.getType().getName());
            if (symbol.getType().isArray())
                methodKey.append("[]");
        }
        this.methods.put(methodKey.toString(), new JmmMethodSymbolTable(returnType));
    }
    */

    public void addMethod(String methodName, Type returnType) {
        if (this.methods.containsKey(methodName)) {
            Exception e = new Exception("The method with name \"" + methodName + "\" is already defined in the class scope");
            this.addError(e);
        }
        this.methods.put(methodName, new JmmMethodSymbolTable(returnType));
    }

    public void addField(Symbol field) {
        for (Symbol symbol : this.fields) {
            if (symbol.getName().equals(field.getName())) {
                Exception e = new Exception("The attribute with name \"" + field.getName() + "\" is already defined in the class scope");
                this.addError(e);
            }
        }
        this.fields.add(field);
    }

    public void addParameter(String methodName, Symbol argument) {
        try {
            this.methods.get(methodName).addParameter(argument);
        } catch (Exception exception) {
            this.addError(exception);
        }
    }

    public void addLocalVariable(String methodName, Symbol localVariable) {
        try {
            this.methods.get(methodName).addLocalVariable(localVariable);
        } catch (Exception exception) {
            this.addError(exception);
        }
    }

    public String toString(String spaces) {
        StringBuilder b = new StringBuilder();

        b.append(spaces);
        b.append("Class: ");
        b.append(this.className);
        b.append("\n");

        if (this.superClassName != null) {
            b.append(spaces);
            b.append("Extends: ");
            b.append(this.superClassName);
            b.append("\n");
        }

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