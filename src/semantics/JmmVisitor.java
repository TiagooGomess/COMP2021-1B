package semantics;

import nodes.Class;
import nodes.Method;
import nodes.SymbolTable;
import nodes.expression.Expression;
import nodes.expression.Terminal;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
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

public class JmmVisitor extends PreorderJmmVisitor<JmmSymbolTable, Expression> {
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

        //addVisit("Operation", this::defaultFunction);
        //addVisit("Method", this::defaultFunction);
        //addVisit("Construction", this::defaultFunction);
        //addVisit("Literal", this::dealWithLiteral);
        //addVisit("Variable", this::dealWithVariable);
        //addVisit("This", this::dealWithVariable);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    // ----------------------------------------------------------------
    // Class
    // ----------------------------------------------------------------

    private Expression dealWithImport(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        symbolTable.addImport(Class.fromNode(node));
        return null;
    }

    private Expression dealWithClass(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        symbolTable.setMainClass(Class.fromNode(node));
        return null;
    }

    private Expression dealWithAttribute(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        symbolTable.addField(Terminal.fromDeclaration(node));
        return null;
    }

    // ----------------------------------------------------------------
    // Methods
    // ----------------------------------------------------------------

    private Expression dealWithMethod(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        currentMethod = Method.fromNode(node);
        symbolTable.addMethod(currentMethod);
        return null;
    }

    private Expression dealWithArgument(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        currentMethod.addParameter(Terminal.fromDeclaration(node));
        return null;
    }

    private Expression dealWithLocalVariable(JmmNode node, JmmSymbolTable jmmSymbolTable) {
        currentMethod.addLocalVariable(Terminal.fromDeclaration(node));
        return null;
    }


    // ----------------------------------------------------------------
    // Expressions
    // ----------------------------------------------------------------

    private Expression dealWithLiteral(JmmNode jmmNode, JmmSymbolTable jmmSymbolTable) {
        return Terminal.fromLiteral(jmmNode);
    }

    private Expression dealWithVariable(JmmNode jmmNode, JmmSymbolTable jmmSymbolTable) {
        return Terminal.fromVariable(this.currentMethod, jmmNode);
    }
}

