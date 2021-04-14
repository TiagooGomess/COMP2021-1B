package semantics;

import nodes.Class;
import nodes.method.Method;
import nodes.SymbolTable;
import nodes.value.exception.JmmException;
import nodes.value.Value;
import nodes.value.Terminal;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.JmmNode;

// public String[] jjtNodeName = { "skipParenthesis", "Program",
// "ImportDeclaration", "void", "Class", "Attributes",
// "Methods", "MethodDeclaration", "Arguments", "Argument", "Body", "Return",
// "VariableDeclaration",
// "AttributeDeclaration", "Block", "If", "Then", "Else", "While", "Assignment",
// "Condition", "Operation",
// "Position", "Access", "Call", "Method", "Construction", "Size", "Literal",
// "Variable", "This",
// "Operator", };

public class JmmVisitor extends PreorderJmmVisitor<JmmSymbolTable, Value> {
    private final SymbolTable symbolTable = new SymbolTable();
    private Method currentMethod = null;

    public JmmVisitor() {
        super();

        addVisit("ImportDeclaration", this::dealWithImport);
        addVisit("Class", this::dealWithClass);
        addVisit("MethodDeclaration", this::dealWithMethod);
        addVisit("AttributeDeclaration", this::dealWithAttribute);
        addVisit("Argument", this::dealWithArgument);
        addVisit("MainArgument", this::dealWithArgument);
        addVisit("VariableDeclaration", this::dealWithLocalVariable);

        addVisit("Operation", this::dealWithValue);
        addVisit("Access", this::dealWithValue);
        addVisit("Call", this::dealWithValue);
        addVisit("Construction", this::dealWithValue);
        addVisit("Literal", this::dealWithValue);
        addVisit("Variable", this::dealWithValue);
        addVisit("This", this::dealWithValue);

        //setDefaultVisit(this::defaultVisit);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    // ----------------------------------------------------------------
    // Class
    // ----------------------------------------------------------------

    private Value dealWithImport(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        symbolTable.addImport(Class.fromNode(node));
        return null;
    }

    private Value dealWithClass(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        symbolTable.setMainClass(Class.fromNode(node));
        return null;
    }

    private Value dealWithAttribute(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        symbolTable.addField(Terminal.fromDeclaration(node));
        return null;
    }

    // ----------------------------------------------------------------
    // Methods
    // ----------------------------------------------------------------

    private Value dealWithMethod(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        currentMethod = Method.fromDeclaration(node);
        symbolTable.addMethod(currentMethod);
        return null;
    }

    private Value dealWithArgument(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        currentMethod.addParameter(Terminal.fromDeclaration(node));
        return null;
    }

    private Value dealWithLocalVariable(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        currentMethod.addLocalVariable(Terminal.fromDeclaration(node));
        return null;
    }

    // ----------------------------------------------------------------
    // Expressions
    // ----------------------------------------------------------------

    private Value dealWithValue(JmmNode jmmNode, JmmSymbolTable jmmSymbolTable) {
        try {
            return Value.fromNode(symbolTable, currentMethod, jmmNode, null);
        } catch (JmmException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}

