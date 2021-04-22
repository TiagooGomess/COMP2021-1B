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

import java.util.*;

public class JmmVisitor extends PreorderJmmVisitor<SymbolTable, Value> {
    private final SymbolTable symbolTable = new SymbolTable();
    private Method currentMethod = null;

    private final Map<Method, List<JmmNode>> methodStatements = new HashMap<>();
    private final Map<JmmNode, Statement> statementNode = new HashMap<>();

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

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    // ----------------------------------------------------------------
    // Class
    // ----------------------------------------------------------------

    private Value dealWithImport(JmmNode node, SymbolTable jmmSymbolTable) {
        symbolTable.addImport(Class.fromNode(node));
        return null;
    }

    private Value dealWithClass(JmmNode node, SymbolTable jmmSymbolTable) {
        symbolTable.setMainClass(Class.fromNode(node));
        return null;
    }

    private Value dealWithAttribute(JmmNode node, SymbolTable jmmSymbolTable) {
        symbolTable.addField(Terminal.fromDeclaration(node));
        return null;
    }

    // ----------------------------------------------------------------
    // Methods
    // ----------------------------------------------------------------

    private Value dealWithMethod(JmmNode node, SymbolTable jmmSymbolTable) {
        currentMethod = Method.fromDeclaration(symbolTable.getClass(null), node);
        symbolTable.addMethod(currentMethod);
        methodStatements.put(currentMethod, new ArrayList<>());
        return null;
    }

    private Value dealWithArgument(JmmNode node, SymbolTable jmmSymbolTable) {
        currentMethod.addParameter(Terminal.fromDeclaration(node));
        return null;
    }

    private Value dealWithLocalVariable(JmmNode node, SymbolTable jmmSymbolTable) {
        currentMethod.addLocalVariable(Terminal.fromDeclaration(node));
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
        for (Map.Entry<Method, List<JmmNode>> entry : this.methodStatements.entrySet()) {
            Method method = entry.getKey();
            for (JmmNode statementNode : entry.getValue()) {
                try {
                    Statement statement = Statement.fromNode(this.symbolTable, method, statementNode);
                    this.statementNode.put(statementNode, statement);
                } catch (JmmException e) {
                    this.symbolTable.addReport(e);
                }
            }
        }
    }

    public String getOllir() {
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

