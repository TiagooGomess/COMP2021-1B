package analysis.symbol;

import analysis.method.Method;
import analysis.value.Terminal;
import analysis.value.Value;
import exception.JmmException;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolTable implements pt.up.fe.comp.jmm.analysis.table.SymbolTable {
    private final Program program = new Program();
    private final List<Report> reports = new ArrayList<>();
    public static int auxiliaryVariableNumber = 1;

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    @Override
    public List<String> getImports() {
        List<String> result = new ArrayList<>();
        for (Class externalClass : this.program.getExternalClasses())
            result.add(externalClass.getImportName());
        return result;
    }

    @Override
    public String getClassName() {
        return this.program.getMainClass().getName();
    }

    @Override
    public String getSuper() {
        return this.program.getMainClass().getSuperName();
    }

    @Override
    public List<Symbol> getFields() {
        ArrayList<Symbol> result = new ArrayList<>();
        for (Terminal terminal : this.program.getMainClass().getAttributes())
            result.add(terminal.getSymbol());
        return result;
    }

    @Override
    public List<String> getMethods() {
        List<String> result = new ArrayList<>();
        for (Method method : this.program.getMainClass().getMethods()) {
            result.add(method.getName());
        }
        return result;
    }

    @Override
    public Type getReturnType(String methodName) {
        return this.program.getMainClass().getReturnType(methodName);
    }

    @Override
    public List<Symbol> getParameters(String methodName) {
        ArrayList<Symbol> result = new ArrayList<>();
        for (Terminal terminal : this.program.getMainClass().getParameters(methodName))
            result.add(terminal.getSymbol());
        return result;
    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        ArrayList<Symbol> result = new ArrayList<>();
        for (Terminal terminal : this.program.getMainClass().getLocalVariables(methodName))
            result.add(terminal.getSymbol());
        return result;
    }

    public List<Report> getReports() {
        return this.reports;
    }

    public List<Method> getMethod(String className, String methodName) {
        return this.program.getMethod(className, methodName);
    }

    public Class getClass(String className) {
        if (className == null)
            return this.program.getMainClass();
        return this.program.getClass(className);
    }

    public List<Method> getMethod(String methodName) {
        return this.program.getMethod(methodName);
    }

    public Value getVariable(Method scopeMethod, String variableName) {
        return this.program.getVariable(scopeMethod, variableName);
    }

    public Type getType(String typeName) throws JmmException {
        return this.program.getType(typeName);
    }

    // ----------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------

    public void setMainClass(Class mainClass) {
        this.program.setMainClass(mainClass);
    }

    // ----------------------------------------------------------------
    // Adders
    // ----------------------------------------------------------------

    public void addReport(JmmException exception) {
        reports.add(new Report(exception.isWarning() ? ReportType.WARNING : ReportType.ERROR, Stage.SEMANTIC, exception.getLine(), exception.getColumn(), exception.getMessage()));
    }

    public void addImport(Class externalClass) {
        this.program.addExternalClass(externalClass);
    }

    public void addField(Terminal terminal) throws JmmException {
        this.program.getMainClass().addAttribute(terminal);
    }

    public void addMethod(Method method) throws JmmException {
        this.program.getMainClass().addMethod(method);
    }

    // ----------------------------------------------------------------
    // Output
    // ----------------------------------------------------------------

    @Override
    public String toString() {
        return this.program.toString();
    }

    @Override
    public String print() {
        var builder = new StringBuilder();

        builder.append("Class: " + getClassName() + "\n");
        var superClass = getSuper() != null ? getSuper() : "java.lang.Object";
        builder.append("Super: " + superClass + "\n");
        builder.append("\nImports:");
        var imports = getImports();

        if (imports.isEmpty()) {
            builder.append(" <no imports>\n");
        } else {
            builder.append("\n");
            imports.forEach(fullImport -> builder.append(" - " + fullImport + "\n"));
        }

        var fields = getFields();
        builder.append("\nFields:");
        if (fields.isEmpty()) {
            builder.append(" <no fields>\n");
        } else {
            builder.append("\n");
            fields.forEach(field -> builder.append(" - " + field.print() + "\n"));
        }

        var methods = this.program.getMainClass().getMethods();
        builder.append("\nMethods: " + methods.size() + "\n");
        for (Method method : methods) {
            var returnType = method.getReturnType();
            var params = method.getParameters();
            builder.append(" - " + returnType.print() + " " + method.getName() + "(");
            var ref = new Object() {
                int i = 0;
            };
            var paramsString = params.stream().map(param -> param != null ? param.print() + " " + ref.i++ : "<null param>")
                    .collect(Collectors.joining(", "));
            builder.append(paramsString + ")\n");
        }

        return builder.toString();
    }
}
