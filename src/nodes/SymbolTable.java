package nodes;

import nodes.expression.Terminal;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;

public class SymbolTable implements pt.up.fe.comp.jmm.analysis.table.SymbolTable {
    private final Program program = new Program();
    private final List<Report> reports = new ArrayList<>();

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
        return new ArrayList<>(this.program.getMainClass().getAttributes());
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
        return new ArrayList<>(this.program.getMainClass().getParameters(methodName));
    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        return new ArrayList<>(this.program.getMainClass().getLocalVariables(methodName));
    }

    public Type getVariableType(String scopeMethodName, String variableName) {
        return this.program.getMainClass().getVariableType(scopeMethodName, variableName);
    }

    public Method getMethod(String methodName) {
        return this.program.getMethod(methodName);
    }

    public Method getMethod(String className, String methodName) {
        return this.program.getMethod(className, methodName);
    }

    public List<Report> getReports() {
        return this.reports;
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

    public void addError(Exception e) {
        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, e.getStackTrace()[0].getLineNumber(), e.getMessage()));
    }

    public void addImport(Class externalClass) {
        this.program.addExternalClass(externalClass);
    }

    public void addField(Terminal terminal) {
        this.program.getMainClass().addAttribute(terminal);
    }

    public void addMethod(Method method) {
        this.program.getMainClass().addMethod(method);
    }

    // ----------------------------------------------------------------
    // Output
    // ----------------------------------------------------------------

    @Override
    public String toString() {
        return this.program.toString();
    }
}
