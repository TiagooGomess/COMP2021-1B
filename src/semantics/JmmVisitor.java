package semantics;

import nodes.Class;
import nodes.Program;
import nodes.method.Method;
import nodes.SymbolTable;
import nodes.value.exception.JmmException;
import nodes.value.Value;
import nodes.value.Terminal;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.JmmNode;

import java.util.*;

// public String[] jjtNodeName = { "skipParenthesis", "Program",
// "ImportDeclaration", "void", "Class", "Attributes",
// "Methods", "MethodDeclaration", "Arguments", "Argument", "Body", "Return",
// "VariableDeclaration", "AttributeDeclaration",
//
// "Block", "If", "Then", "Else", "While", "Assignment",
// "Condition",
//
// "Operation", "Operator"
// "Position", "Access", "Call", "Method", "Construction", "Size",
// "Literal", "Variable", "This" };

public class JmmVisitor extends PreorderJmmVisitor<SymbolTable, Value> {
    private final SymbolTable symbolTable = new SymbolTable();
    private final static List<String> statementTypes = Arrays.asList("Body", "Return", "Block", "If", "Then", "Else", "While");
    private final static List<String> valueTypes = Arrays.asList("Operation", "Access", "Call", "Construction", "Literal", "Variable", "This");
    private Method currentMethod = null;
    private Map<Method, List<JmmNode>> methodValues = new HashMap<>();

    public JmmVisitor() {
        super();

        addVisit("ImportDeclaration", this::dealWithImport);
        addVisit("Class", this::dealWithClass);
        addVisit("MethodDeclaration", this::dealWithMethod);
        addVisit("AttributeDeclaration", this::dealWithAttribute);
        addVisit("Argument", this::dealWithArgument);
        addVisit("MainArgument", this::dealWithArgument);
        addVisit("VariableDeclaration", this::dealWithLocalVariable);

        addVisit("Condition", this::dealWithCondition);
        addVisit("Assignment", this::dealWithAssignment);

        for (String valueParent : statementTypes)
            addVisit(valueParent, this::dealWithValue);

        //setDefaultVisit(this::defaultVisit);
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
        currentMethod = Method.fromDeclaration(node);
        symbolTable.addMethod(currentMethod);
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
    // Expressions
    // ----------------------------------------------------------------

    private Value dealWithAssignment(JmmNode jmmNode, SymbolTable jmmSymbolTable) {
        List<JmmNode> operators = jmmNode.getChildren();

        Terminal variable;
        try {
            variable = (Terminal) Terminal.fromVariable(this.symbolTable, this.currentMethod, operators.get(0));
        } catch (JmmException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (java.lang.NullPointerException e) {
            System.out.println("Invalid assignment, left side is not a variable");
            return null;
        }

        Value value;
        try {
            value = Value.fromNode(this.symbolTable, this.currentMethod, operators.get(1), variable.getReturnType());
        } catch (JmmException e) {
            System.out.println(e.getMessage());
            return null;
        }

        if (!variable.getReturnType().equals(value.getReturnType())) {
            JmmException e = JmmException.invalidAssignment(variable.getName(), variable.getType(), value.getReturnType());
            System.out.println(e.getMessage());
        }

        return null;
    }

    private Value dealWithCondition(JmmNode jmmNode, SymbolTable jmmSymbolTable) {
        JmmNode expressionNode = jmmNode.getChildren().get(0);
        Value value;
        try {
            value = Value.fromNode(this.symbolTable, this.currentMethod, expressionNode, Program.BOOL_TYPE);
        } catch (JmmException e) {
            System.out.println(e.getMessage());
            return null;
        }

        if (!value.getReturnType().equals(Program.BOOL_TYPE)) {
            JmmException e = JmmException.invalidCondition(value.getReturnType());
            System.out.println(e.getMessage());
        }

        return null;
    }

    private Value dealWithValue(JmmNode jmmNode, SymbolTable jmmSymbolTable) {
        List<JmmNode> valueNodes = new ArrayList<>();
        for (JmmNode child : jmmNode.getChildren())
            if (JmmVisitor.valueTypes.contains(child.getKind()))
                valueNodes.add(child);

        if (!this.methodValues.containsKey(currentMethod))
            this.methodValues.put(currentMethod, new ArrayList<>());
        this.methodValues.get(currentMethod).addAll(valueNodes);

        return null;
    }

    // ----------------------------------------------------------------
    // Analyse values
    // ----------------------------------------------------------------

    public void analyseMethodValues() {
        for (Map.Entry<Method, List<JmmNode>> entry : this.methodValues.entrySet()) {
            Method method = entry.getKey();
            for (JmmNode valueNode : entry.getValue()) {
                try {
                    Value value = Value.fromNode(this.symbolTable, method, valueNode, null);
                } catch (JmmException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

}

