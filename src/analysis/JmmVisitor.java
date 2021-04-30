package analysis;

import analysis.statement.Statement;
import analysis.symbol.Class;
import analysis.method.Method;
import analysis.symbol.SymbolTable;
import exception.JmmException;
import analysis.value.Value;
import analysis.value.Terminal;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.report.Report;

import java.util.*;

public class JmmVisitor extends PreorderJmmVisitor<SymbolTable, Value> {
    private final SymbolTable symbolTable = new SymbolTable();
    private Method currentMethod = null;

    private final Map<Method, List<JmmNode>> methodStatements = new HashMap<>();
    private final Map<JmmNode, Statement> statementNode = new HashMap<>();

    public Value noVisit(JmmNode jmmNode, SymbolTable data) {
        return null;
    }

    private void stopVisiting() {
        addVisit("ImportDeclaration", this::noVisit);
        addVisit("Class", this::noVisit);
        addVisit("MethodDeclaration", this::noVisit);
        addVisit("AttributeDeclaration", this::noVisit);
        addVisit("Argument", this::noVisit);
        addVisit("MainArgument", this::noVisit);
        addVisit("VariableDeclaration", this::noVisit);
        addVisit("Body", this::noVisit);
    }

    public JmmVisitor() {
        addVisit("ImportDeclaration", this::dealWithImport);
        addVisit("Class", this::dealWithClass);
        addVisit("MethodDeclaration", this::dealWithMethod);
        addVisit("AttributeDeclaration", this::dealWithAttribute);
        addVisit("Argument", this::dealWithArgument);
        addVisit("MainArgument", this::dealWithArgument);
        addVisit("VariableDeclaration", this::dealWithLocalVariable);

        addVisit("Body", this::dealWithBody);
    }

    private void addReport(JmmException e) {
        this.symbolTable.addReport(e);
        stopVisiting();
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public List<Report> getReports() {
        return this.symbolTable.getReports();
    }

    // ----------------------------------------------------------------
    // Class
    // ----------------------------------------------------------------

    private Value dealWithImport(JmmNode node, SymbolTable jmmSymbolTable) {
        try {
            symbolTable.addImport(Class.fromNode(this.symbolTable, node));
        } catch (JmmException e) {
            this.addReport(e);
        }
        return null;
    }

    private Value dealWithClass(JmmNode node, SymbolTable jmmSymbolTable) {
        try {
            symbolTable.setMainClass(Class.fromNode(this.symbolTable, node));
        } catch (JmmException e) {
            this.addReport(e);
        }
        return null;
    }

    private Value dealWithAttribute(JmmNode node, SymbolTable jmmSymbolTable) {
        try {
            symbolTable.addField(Terminal.fromDeclaration(this.symbolTable, node));
        } catch (JmmException e) {
            this.addReport(e);
        }
        return null;
    }

    // ----------------------------------------------------------------
    // Methods
    // ----------------------------------------------------------------

    private void verifyMethods() throws JmmException {
        if (this.currentMethod != null) {
            for (Method method : this.symbolTable.getClass(null).getMethods()) {
                if (method == this.currentMethod)
                    continue;
                if (method.getSignature().equals(this.currentMethod.getSignature()))
                    throw JmmException.methodAlreadyDefined(method.getName(), this.symbolTable.getClassName(), method.getParameters());
            }
        }
    }

    private Value dealWithMethod(JmmNode node, SymbolTable jmmSymbolTable) {
        try {
            verifyMethods();
            currentMethod = Method.fromDeclaration(symbolTable, symbolTable.getClass(null), node);
            symbolTable.addMethod(currentMethod);
            methodStatements.put(currentMethod, new ArrayList<>());
        } catch (JmmException e) {
            this.addReport(e);
        }
        return null;
    }

    private Value dealWithArgument(JmmNode node, SymbolTable jmmSymbolTable) {
        try {
            currentMethod.addParameter(Terminal.fromDeclaration(this.symbolTable, node));
        } catch (JmmException e) {
            this.addReport(e);
        }
        return null;
    }

    private Value dealWithLocalVariable(JmmNode node, SymbolTable jmmSymbolTable) {
        try {
            currentMethod.addLocalVariable(Terminal.fromDeclaration(this.symbolTable, node));
        } catch (JmmException e) {
            this.addReport(e);
        }
        return null;
    }

    // ----------------------------------------------------------------
    // Body statements
    // ----------------------------------------------------------------

    private Value dealWithBody(JmmNode jmmNode, SymbolTable jmmSymbolTable) {
        for (JmmNode child : jmmNode.getChildren())
            if (!child.getKind().equals("VariableDeclaration"))
                methodStatements.get(currentMethod).add(child);
        return null;
    }

    // ----------------------------------------------------------------
    // Analyse values
    // ----------------------------------------------------------------

    public void analyseMethodValues() {
        if (!this.symbolTable.getReports().isEmpty())
            return;

        try {
            verifyMethods();
        } catch (JmmException e) {
            this.addReport(e);
        }

        for (Map.Entry<Method, List<JmmNode>> entry : this.methodStatements.entrySet()) {
            Method method = entry.getKey();
            for (JmmNode statementNode : entry.getValue()) {
                try {
                    Statement statement = Statement.fromNode(this.symbolTable, method, statementNode);
                    this.statementNode.put(statementNode, statement);
                } catch (JmmException e) {
                    this.addReport(e);
                }
            }
        }
    }

    public String getOllir() {
        if (!this.symbolTable.getReports().isEmpty())
            return null;

        StringBuilder builder = new StringBuilder();

        for (Method method : this.symbolTable.getClass(null).getMethods()) {
            StringBuilder declaration = new StringBuilder(method.getOllir() + " {");
            boolean specialCase = !methodStatements.containsKey(method);

            if (specialCase) {
                declaration.append("\n  invokespecial(this, \"<init>\").V;");
            } else {
                for (JmmNode node : this.methodStatements.get(method))
                    if (statementNode.containsKey(node)) {
                        Statement statement = statementNode.get(node);
                        if (statement == null)
                            continue;
                        String ollir = statement.getOllir();
                        if (ollir != null)
                            declaration.append("\n  ").append(ollir.replace("\n", "\n  "));
                    }
            }
            if (declaration.toString().endsWith(":"))
                declaration.append("\n  ret.V;");
            declaration.append("\n}\n");

            if (specialCase)
                builder.insert(0, declaration);
            else
                builder.append(declaration);
        }

        for (Terminal attribute : symbolTable.getClass(null).getAttributes())
            if (!attribute.getName().equals("this"))
                builder.insert(0, ".field private " + attribute.getOllir() + ";\n");

        builder.insert(0, symbolTable.getClassName() + " {\n");
        String result = builder.toString().strip().replace("\n", "\n  ");
        result += "\n}";
        return result;
    }
}

