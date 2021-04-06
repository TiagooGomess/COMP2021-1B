import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.List;
import java.util.ArrayList;
import java.lang.Void;

public class JmmVisitor extends PreorderJmmVisitor<JmmSymbolTable, Void> {
    private final JmmSymbolTable symbolTable = new JmmSymbolTable();
    private static String currentMethod = null;

    // public String[] jjtNodeName = { "skipParenthesis", "Program",
    // "ImportDeclaration", "void", "Class", "Attributes",
    // "Methods", "MethodDeclaration", "Arguments", "Argument", "Body", "Return",
    // "VariableDeclaration",
    // "AttributeDeclaration", "Block", "If", "Then", "Else", "While", "Assignment",
    // "Condition", "Operation",
    // "Position", "Access", "Call", "Method", "Construction", "Size", "Literal",
    // "Variable", "This",
    // "Operator", };

    public JmmVisitor() {
        super();

        addVisit("ImportDeclaration", this::dealWithImport);
        addVisit("Class", this::dealWithClass);
        addVisit("MethodDeclaration", this::dealWithMethod);
        addVisit("AttributeDeclaration", this::dealWithAttribute);
        addVisit("Argument", this::dealWithArgument);
        addVisit("MainArgument", this::dealWithArgument);
        addVisit("VariableDeclaration", this::dealWithLocalVariable);
    }

    public JmmSymbolTable getSymbolTable() {
        return symbolTable;
    }

    private Type typeFromString(String typeName) {
        int size = typeName.length();
        boolean isArray = false;
        if (typeName.substring(size - 2, size).equals("[]")) {
            isArray = true;
            typeName = typeName.substring(0, size - 2);
        }
        return new Type(typeName, isArray);
    }

    private Void dealWithImport(JmmNode node, JmmSymbolTable parent) {
        symbolTable.addImport(node.get("name"));
        return null;
    }

    private Void dealWithClass(JmmNode node, JmmSymbolTable parent) {
        String className = node.get("name");
        if (node.getAttributes().contains("extends")) {
            symbolTable.setSuper(node.get("extends"));
        }
        symbolTable.setClassName(className);
        return null;
    }

    private Void dealWithAttribute(JmmNode node, JmmSymbolTable parent) {
        symbolTable.addField(new Symbol(typeFromString(node.get("type")), node.get("name")));
        return null;
    }

    private Void dealWithMethod(JmmNode node, JmmSymbolTable parent) {
        String methodName = node.get("name");
        currentMethod = methodName;
        Type returnType = new Type(node.get("type"), false);
        symbolTable.addMethod(methodName, returnType);
        return null;
    }

    private Void dealWithArgument(JmmNode node, JmmSymbolTable parent) {
        Type type = typeFromString(node.get("type"));
        Symbol variableSymbol = new Symbol(type, node.get("name"));
        symbolTable.addParameter(currentMethod, variableSymbol);
        return null;
    }

    private Void dealWithLocalVariable(JmmNode node, JmmSymbolTable parent) {
        Type type = typeFromString(node.get("type"));
        Symbol variableSymbol = new Symbol(type, node.get("name"));
        symbolTable.addLocalVariable(currentMethod, variableSymbol);
        return null;
    }
}