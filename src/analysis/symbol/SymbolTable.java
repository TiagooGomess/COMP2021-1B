package analysis.symbol;

import analysis.method.Method;
import analysis.value.Terminal;
import analysis.value.Value;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.Report;

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

    // ----------------------------------------------------------------
    // Setters
    // ----------------------------------------------------------------

    public void setMainClass(Class mainClass) {
        this.program.setMainClass(mainClass);
    }

    // ----------------------------------------------------------------
    // Adders
    // ----------------------------------------------------------------

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
